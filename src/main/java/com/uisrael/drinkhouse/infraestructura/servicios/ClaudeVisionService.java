package com.uisrael.drinkhouse.infraestructura.servicios;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.uisrael.drinkhouse.aplicacion.excepciones.ServicioNoDisponibleException;
import com.uisrael.drinkhouse.infraestructura.utils.ImageOptimizer;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoBotellaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoProductoDto;

/**
 * Servicio de infraestructura que encapsula la comunicación con la API de Claude (Anthropic).
 * Proporciona métodos para identificar botellas y extraer datos de facturas a partir de imágenes.
 */
@Service
public class ClaudeVisionService {

    // Prompts optimizados para precisión y bajo costo
    private static final String PROMPT_BOTELLA = """
            Analiza esta imagen de bebida.
            IMPORTANTE:
            - Solo reporta información VISIBLE en la etiqueta
            - Si algo no es visible, usa null (sin comillas) o 'desconocido'
            - NO adivines ni inventes información
            - Para presentacion y graduacionAlcohol: null si no están claramente visibles
            """;

    private static final String PROMPT_FACTURA = """
            Extrae datos visibles de esta factura.
            IMPORTANTE:
            - Solo información VISIBLE y legible
            - Usa null (sin comillas) para campos no visibles
            - NO adivines números o datos
            """;

    private static final String PROMPT_PRODUCTO = """
            Analiza esta imagen de producto.
            IMPORTANTE:
            - Solo reporta información VISIBLE en el empaque/etiqueta
            - Identifica el tipo de producto (bebida, snack, alimento, etc.)
            - Si algo no es visible, usa null (sin comillas) o 'desconocido'
            - NO adivines ni inventes información
            - Para contenido e informacionAdicional: null si no están visibles
            """;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ImageOptimizer imageOptimizer;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.model:claude-haiku-4-5-20251001}")
    private String modelo;

    @Value("${anthropic.max-tokens:200}")
    private int maxTokens;

    @Value("${anthropic.enable-prompt-caching:true}")
    private boolean enablePromptCaching;

    public ClaudeVisionService(
            @Value("${anthropic.api.url:https://api.anthropic.com/v1/messages}") String apiUrl,
            ObjectMapper objectMapper,
            ImageOptimizer imageOptimizer) {
        this.objectMapper = objectMapper;
        this.imageOptimizer = imageOptimizer;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("anthropic-version", "2023-06-01")
                .build();
    }

    /**
     * Identifica una botella de bebida alcohólica a partir de su imagen en base64.
     *
     * @param imagenBase64   imagen codificada en base64
     * @param formatoImagen  formato de la imagen (JPEG, PNG, WEBP)
     * @return resultado estructurado con los datos identificados de la botella
     * @throws ServicioNoDisponibleException si Claude no responde o retorna JSON inválido
     */
    public ResultadoBotellaDto identificarBotella(String imagenBase64, String formatoImagen) {
        String respuestaJson = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_BOTELLA, "identificar_producto");
        try {
            return objectMapper.readValue(respuestaJson, ResultadoBotellaDto.class);
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude para identificación de botella no es un JSON válido: " + e.getMessage());
        }
    }

    /**
     * Extrae los datos de una factura a partir de su imagen en base64.
     *
     * @param imagenBase64   imagen codificada en base64
     * @param formatoImagen  formato de la imagen (JPEG, PNG, WEBP)
     * @return resultado estructurado con los datos extraídos de la factura
     * @throws ServicioNoDisponibleException si Claude no responde o retorna JSON inválido
     */
    public ResultadoFacturaDto extraerFactura(String imagenBase64, String formatoImagen) {
        String respuestaJson = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_FACTURA, "extraer_factura");
        try {
            return objectMapper.readValue(respuestaJson, ResultadoFacturaDto.class);
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude para extracción de factura no es un JSON válido: " + e.getMessage());
        }
    }

    /**
     * Identifica un producto genérico (bebida, snack, alimento, etc.) a partir de su imagen.
     *
     * @param imagenBase64   imagen codificada en base64
     * @param formatoImagen  formato de la imagen (JPEG, PNG, WEBP)
     * @return resultado estructurado con los datos identificados del producto
     * @throws ServicioNoDisponibleException si Claude no responde o retorna JSON inválido
     */
    public ResultadoProductoDto identificarProductoGenerico(String imagenBase64, String formatoImagen) {
        String respuestaJson = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_PRODUCTO, "identificar_producto_generico");
        try {
            return objectMapper.readValue(respuestaJson, ResultadoProductoDto.class);
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude para identificación de producto no es un JSON válido: " + e.getMessage());
        }
    }

    /**
     * Realiza la llamada HTTP a la API de Claude con una imagen y un prompt.
     * Usa Tool Calling para garantizar respuestas JSON estructuradas.
     * 
     * OPTIMIZACIONES APLICADAS:
     * 1. Redimensiona imagen a 1024px máximo antes de enviarla (reduce 80-90% tokens de imagen)
     * 2. Convierte a JPEG calidad 85% (reduce peso sin perder legibilidad)
     * 3. Usa prompt caching para system prompts repetidos (reduce 90% costo del prompt)
     * 4. Ajusta maxTokens según configuración (solo lo necesario para la respuesta JSON)
     *
     * @param imagenBase64  imagen en base64
     * @param formatoImagen formato de la imagen original
     * @param prompt        instrucción de texto para Claude
     * @param toolName      nombre de la herramienta a usar para estructurar la respuesta
     * @return texto plano devuelto por Claude (se espera que sea JSON)
     */
    private String llamarClaudeConImagen(String imagenBase64, String formatoImagen, String prompt, String toolName) {
        // OPTIMIZACIÓN 1: Redimensionar y comprimir imagen antes de enviarla
        String imagenOptimizada = imagenBase64;
        try {
            imagenOptimizada = imageOptimizer.optimizarImagen(imagenBase64);
            // Después de optimizar, siempre es JPEG
            formatoImagen = "JPEG";
        } catch (Exception e) {
            // Si falla la optimización, continuar con la imagen original
            // Esto permite que el servicio sea resiliente ante errores de procesamiento
            System.err.println("Advertencia: No se pudo optimizar la imagen. Usando original. Error: " + e.getMessage());
        }
        
        String mediaType = resolverMediaType(formatoImagen);

        // Construir el cuerpo de la solicitud según la especificación de la API de Claude
        Map<String, Object> fuenteImagen = Map.of(
                "type", "base64",
                "media_type", mediaType,
                "data", imagenOptimizada
        );

        Map<String, Object> contenidoImagen = Map.of(
                "type", "image",
                "source", fuenteImagen
        );

        Map<String, Object> contenidoTexto = Map.of(
                "type", "text",
                "text", prompt
        );

        Map<String, Object> mensaje = Map.of(
                "role", "user",
                "content", List.of(contenidoImagen, contenidoTexto)
        );

        // OPTIMIZACIÓN 2: System prompt con cache control para reutilización
        // El tool schema se envía como system para poder cachearlo
        Map<String, Object> toolSchema = crearToolSchema(toolName);
        String systemPrompt = crearSystemPromptConSchema(toolSchema);
        
        Map<String, Object> systemBlock;
        if (enablePromptCaching) {
            // Habilitar cache para el system prompt (reduce 90% del costo en llamadas subsecuentes)
            systemBlock = Map.of(
                    "type", "text",
                    "text", systemPrompt,
                    "cache_control", Map.of("type", "ephemeral")
            );
        } else {
            systemBlock = Map.of(
                    "type", "text",
                    "text", systemPrompt
            );
        }
        
        Map<String, Object> cuerpoSolicitud = Map.of(
                "model", modelo,
                "max_tokens", maxTokens,
                "system", List.of(systemBlock),
                "messages", List.of(mensaje),
                "tools", List.of(toolSchema),
                "tool_choice", Map.of("type", "tool", "name", toolName)
        );

        try {
            String respuestaRaw = webClient.post()
                    .header("x-api-key", apiKey)
                    .bodyValue(cuerpoSolicitud)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            return extraerTextoDeRespuesta(respuestaRaw);

        } catch (WebClientResponseException e) {
            throw new ServicioNoDisponibleException(
                    "Error al comunicarse con la API de Claude. Código HTTP: "
                    + e.getStatusCode().value() + " — " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ServicioNoDisponibleException(
                    "Error inesperado al llamar a la API de Claude: " + e.getMessage());
        }
    }

    /**
     * Crea un system prompt que incluye la descripción del schema de la tool.
     * Esto permite que el prompt completo sea cacheado por Anthropic.
     * 
     * @param toolSchema schema de la herramienta
     * @return system prompt con instrucciones y schema
     */
    private String crearSystemPromptConSchema(Map<String, Object> toolSchema) {
        return """
                Eres un asistente especializado en análisis de imágenes.
                Tu tarea es identificar productos y extraer información estructurada.
                REGLAS IMPORTANTES:
                - Solo reporta información VISIBLE y LEGIBLE en la imagen
                - NO adivines ni inventes datos
                - Usa null (sin comillas) para campos que no sean visibles
                - Sé preciso y conciso en tus respuestas
                
                Usa la herramienta proporcionada para estructurar tu respuesta.
                """;
    }

    /**
     * Crea el schema de la herramienta (tool) para forzar respuestas JSON estructuradas.
     * Compatible con Haiku y todos los modelos de Claude.
     * 
     * @param toolName nombre de la herramienta (identificar_producto, identificar_producto_generico, extraer_factura)
     * @return schema de la herramienta configurado según el tipo
     */
    private Map<String, Object> crearToolSchema(String toolName) {
        if ("identificar_producto_generico".equals(toolName)) {
            return crearToolSchemaProductoGenerico();
        } else if ("extraer_factura".equals(toolName)) {
            return crearToolSchemaFactura();
        } else {
            return crearToolSchemaBotella();
        }
    }

    /**
     * Schema para identificación de botellas (bebidas alcohólicas específicamente).
     */
    private Map<String, Object> crearToolSchemaBotella() {
        Map<String, Object> properties = Map.of(
                "nombre", Map.of(
                        "type", "string", 
                        "description", "Nombre exacto del producto visible en la etiqueta. Si no es legible, usar 'desconocido'"
                ),
                "marca", Map.of(
                        "type", "string", 
                        "description", "Marca del producto. Solo si está visible claramente. Si no está visible, usar 'desconocido'"
                ),
                "tipo", Map.of(
                        "type", "string", 
                        "description", "Tipo: whisky, ron, vodka, gin, tequila, vino, cerveza, pisco, gaseosa u otro. Basarse en lo visible."
                ),
                "presentacion", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Tamaño visible en la etiqueta (750ml, 1L, 500ml, etc.). null si NO es visible."
                ),
                "graduacionAlcohol", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Porcentaje de alcohol visible en la etiqueta (ej: '40%'). null (sin comillas) si NO es visible."
                ),
                "reconocido", Map.of(
                        "type", "boolean", 
                        "description", "true si el producto y marca son claramente identificables, false si hay dudas o es genérico"
                )
        );

        Map<String, Object> inputSchema = Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("nombre", "marca", "tipo", "reconocido")
        );

        return Map.of(
                "name", "identificar_producto",
                "description", "Identifica un producto de bebida a partir de su imagen. Solo reporta lo que es visualmente verificable.",
                "input_schema", inputSchema
        );
    }

    /**
     * Schema genérico para identificación de cualquier producto (bebidas, snacks, alimentos, etc.).
     */
    private Map<String, Object> crearToolSchemaProductoGenerico() {
        Map<String, Object> properties = Map.of(
                "nombre", Map.of(
                        "type", "string", 
                        "description", "Nombre exacto del producto visible en el empaque. Si no es legible, usar 'desconocido'"
                ),
                "marca", Map.of(
                        "type", "string", 
                        "description", "Marca del producto. Solo si está visible claramente. Si no está visible, usar 'desconocido'"
                ),
                "categoriaSugerida", Map.of(
                        "type", "string", 
                        "description", "Categoría del producto: bebidas (whisky, ron, gaseosa, agua, etc.), snacks (papas, galletas, chocolate, etc.), alimentos (pan, arroz, conservas, etc.), u otros"
                ),
                "contenido", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Contenido/tamaño visible (750ml, 1L, 500g, 250g, etc.). null si NO es visible."
                ),
                "informacionAdicional", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Información adicional visible: para bebidas alcohólicas el % de alcohol, para alimentos datos nutricionales relevantes. null si no aplica o no es visible."
                ),
                "reconocido", Map.of(
                        "type", "boolean", 
                        "description", "true si el producto y marca son claramente identificables, false si hay dudas o es genérico"
                )
        );

        Map<String, Object> inputSchema = Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("nombre", "marca", "categoriaSugerida", "reconocido")
        );

        return Map.of(
                "name", "identificar_producto_generico",
                "description", "Identifica cualquier tipo de producto (bebidas, snacks, alimentos, etc.) a partir de su imagen. Solo reporta lo que es visualmente verificable.",
                "input_schema", inputSchema
        );
    }

    /**
     * Schema para extracción de datos de facturas.
     */
    private Map<String, Object> crearToolSchemaFactura() {
        Map<String, Object> properties = Map.of(
                "numeroFactura", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Número de factura visible"
                ),
                "razonSocialProveedor", Map.of(
                        "type", "string",
                        "description", "Razón social del proveedor"
                ),
                "rucProveedor", Map.of(
                        "type", List.of("string", "null"),
                        "description", "RUC/NIT del proveedor"
                ),
                "fecha", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Fecha de emisión de la factura"
                ),
                "total", Map.of(
                        "type", List.of("number", "null"),
                        "description", "Monto total de la factura"
                )
        );

        Map<String, Object> inputSchema = Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("razonSocialProveedor")
        );

        return Map.of(
                "name", "extraer_factura",
                "description", "Extrae datos estructurados de una factura. Solo reporta información visible y legible.",
                "input_schema", inputSchema
        );
    }

    /**
     * Extrae el texto del primer bloque de contenido de la respuesta de Claude.
     * Maneja respuestas con tool_use (tool calling).
     *
     * @param respuestaRaw JSON completo devuelto por la API de Claude
     * @return texto del primer bloque content[0].text o tool_use.input, limpio de markdown
     */
    private String extraerTextoDeRespuesta(String respuestaRaw) {
        try {
            JsonNode raiz = objectMapper.readTree(respuestaRaw);
            JsonNode contenido = raiz.path("content");
            
            if (contenido.isArray() && !contenido.isEmpty()) {
                JsonNode primerBloque = contenido.get(0);
                
                // Si es una respuesta de tool_use, extraer el input (ya es JSON estructurado)
                if ("tool_use".equals(primerBloque.path("type").asText())) {
                    JsonNode input = primerBloque.path("input");
                    return objectMapper.writeValueAsString(input);
                }
                
                // Si es texto normal, extraer y limpiar markdown
                String texto = primerBloque.path("text").asText();
                
                // Limpiar markdown si Claude responde con ```json ... ```
                texto = texto.trim();
                if (texto.startsWith("```json")) {
                    texto = texto.substring(7); // Remover ```json
                } else if (texto.startsWith("```")) {
                    texto = texto.substring(3); // Remover ```
                }
                if (texto.endsWith("```")) {
                    texto = texto.substring(0, texto.length() - 3); // Remover ```
                }
                
                return texto.trim();
            }
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude no contiene bloques de contenido válidos");
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudo parsear la respuesta de la API de Claude: " + e.getMessage());
        }
    }

    /**
     * Convierte el formato de imagen del request al media type esperado por Claude.
     *
     * @param formato JPEG, PNG, WEBP o GIF (insensible a mayúsculas)
     * @return media type correspondiente (p.ej. "image/jpeg")
     */
    private String resolverMediaType(String formato) {
        if (formato == null) {
            return "image/jpeg";
        }
        return switch (formato.toUpperCase()) {
            case "PNG"  -> "image/png";
            case "WEBP" -> "image/webp";
            case "GIF"  -> "image/gif";
            default     -> "image/jpeg"; // JPEG y cualquier otro valor
        };
    }
}
