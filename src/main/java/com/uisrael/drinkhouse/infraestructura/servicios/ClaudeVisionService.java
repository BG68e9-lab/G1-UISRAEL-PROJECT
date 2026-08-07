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
import com.uisrael.drinkhouse.infraestructura.utils.PdfToImageConverter;
import com.uisrael.drinkhouse.presentacion.dto.response.RespuestaClaudeDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoBotellaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoProductoDto;

@Service
public class ClaudeVisionService {

    private static final String PROMPT_BOTELLA = """
            Analiza esta imagen de bebida.
            IMPORTANTE:
            - Solo reporta información VISIBLE en la etiqueta
            - Si algo no es visible, usa null (sin comillas) o 'desconocido'
            - NO adivines ni inventes información
            - Para presentacion y graduacionAlcohol: null si no están claramente visibles
            """;

    private static final String PROMPT_FACTURA = """
            Extrae datos visibles de esta factura ecuatoriana.
            IMPORTANTE:
            - Solo información VISIBLE y legible
            - Usa null (sin comillas) para campos no visibles
            - NO adivines números o datos
            
            INSTRUCCIONES ESPECÍFICAS:
            - FECHA: Busca cualquiera de estos textos:
              * "Fecha Emisión:" seguido de fecha DD/MM/YYYY
              * "FECHA Y HORA DE AUTORIZACIÓN" seguido de DD/MM/YYYY HH:MM:SS
              * "Fecha de Emisión" o similar
              La fecha debe convertirse a formato YYYY-MM-DD para el campo fechaFactura
            - TOTAL: Está en la parte inferior derecha, busca:
              * "VALOR TOTAL" seguido de $ y monto
              * "Total" o "TOTAL" seguido de cifra
              Extrae solo el número (sin $ ni símbolos) para el campo totalFactura
            - PRODUCTOS: En la tabla central de la factura, cada fila tiene: código, cantidad, descripción y precios
            - RUC: Número de 13 dígitos en la parte superior (campo rucProveedor)
            - RAZÓN SOCIAL: Nombre del proveedor emisor de la factura (campo razonSocialProveedor)
            - NÚMERO FACTURA: Puede aparecer como "No." o "FACTURA" seguido de números con guiones (ej: 043-003-008450776)
            
            FORMATO DE SALIDA JSON:
            {
              "rucProveedor": "0992526742001",
              "razonSocialProveedor": "DINADEC S.A.",
              "fechaFactura": "2026-07-25",
              "numeroFactura": "043-003-008450776",
              "totalFactura": 86.63,
              "productos": [...],
              "nivelConfianzaGeneral": 95
            }
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
    private final PdfToImageConverter pdfConverter;

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
            ImageOptimizer imageOptimizer,
            PdfToImageConverter pdfConverter) {
        this.objectMapper = objectMapper;
        this.imageOptimizer = imageOptimizer;
        this.pdfConverter = pdfConverter;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("anthropic-version", "2023-06-01")
                .build();
    }

public RespuestaClaudeDto<ResultadoBotellaDto> identificarBotella(String imagenBase64, String formatoImagen) {
        RespuestaClaudeDto<String> respuesta = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_BOTELLA, "identificar_producto");
        try {
            ResultadoBotellaDto resultado = objectMapper.readValue(respuesta.getResultado(), ResultadoBotellaDto.class);
            return RespuestaClaudeDto.<ResultadoBotellaDto>builder()
                    .resultado(resultado)
                    .tokensInput(respuesta.getTokensInput())
                    .tokensOutput(respuesta.getTokensOutput())
                    .tokensCacheRead(respuesta.getTokensCacheRead())
                    .tokensCacheWrite(respuesta.getTokensCacheWrite())
                    .build();
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude para identificación de botella no es un JSON válido: " + e.getMessage());
        }
    }

public RespuestaClaudeDto<ResultadoFacturaDto> extraerFactura(String imagenBase64, String formatoImagen) {
        RespuestaClaudeDto<String> respuesta = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_FACTURA, "extraer_factura");
        try {
            ResultadoFacturaDto resultado = objectMapper.readValue(respuesta.getResultado(), ResultadoFacturaDto.class);
            return RespuestaClaudeDto.<ResultadoFacturaDto>builder()
                    .resultado(resultado)
                    .tokensInput(respuesta.getTokensInput())
                    .tokensOutput(respuesta.getTokensOutput())
                    .tokensCacheRead(respuesta.getTokensCacheRead())
                    .tokensCacheWrite(respuesta.getTokensCacheWrite())
                    .build();
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude para extracción de factura no es un JSON válido: " + e.getMessage());
        }
    }

public RespuestaClaudeDto<ResultadoProductoDto> identificarProductoGenerico(String imagenBase64, String formatoImagen) {
        RespuestaClaudeDto<String> respuesta = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_PRODUCTO, "identificar_producto_generico");
        try {
            ResultadoProductoDto resultado = objectMapper.readValue(respuesta.getResultado(), ResultadoProductoDto.class);
            return RespuestaClaudeDto.<ResultadoProductoDto>builder()
                    .resultado(resultado)
                    .tokensInput(respuesta.getTokensInput())
                    .tokensOutput(respuesta.getTokensOutput())
                    .tokensCacheRead(respuesta.getTokensCacheRead())
                    .tokensCacheWrite(respuesta.getTokensCacheWrite())
                    .build();
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude para identificación de producto no es un JSON válido: " + e.getMessage());
        }
    }

private RespuestaClaudeDto<String> llamarClaudeConImagen(String imagenBase64, String formatoImagen, String prompt, String toolName) {
        String imagenProcesada = imagenBase64;
        if (pdfConverter.esPdf(imagenBase64)) {
            try {
                System.out.println("Detectado PDF, convirtiendo a imagen...");
                imagenProcesada = pdfConverter.convertirPrimeraPaginaAImagen(imagenBase64, 200);
                formatoImagen = "JPEG";
                System.out.println("PDF convertido exitosamente a imagen JPEG");
            } catch (Exception e) {
                throw new ServicioNoDisponibleException(
                    "Error al convertir PDF a imagen: " + e.getMessage());
            }
        }
        
        String imagenOptimizada = imagenProcesada;
        try {
            imagenOptimizada = imageOptimizer.optimizarImagen(imagenProcesada);
            formatoImagen = "JPEG";
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo optimizar la imagen. Usando original. Error: " + e.getMessage());
        }
        
        String mediaType = resolverMediaType(formatoImagen);

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

        Map<String, Object> toolSchema = crearToolSchema(toolName);
        String systemPrompt = crearSystemPromptConSchema(toolSchema);
        
        Map<String, Object> systemBlock;
        if (enablePromptCaching) {
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

            return extraerTextoYTokensDeRespuesta(respuestaRaw);

        } catch (WebClientResponseException e) {
            throw new ServicioNoDisponibleException(
                    "Error al comunicarse con la API de Claude. Código HTTP: "
                    + e.getStatusCode().value() + " — " + e.getResponseBodyAsString());
        } catch (Exception e) {
            throw new ServicioNoDisponibleException(
                    "Error inesperado al llamar a la API de Claude: " + e.getMessage());
        }
    }

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

private Map<String, Object> crearToolSchema(String toolName) {
        if ("identificar_producto_generico".equals(toolName)) {
            return crearToolSchemaProductoGenerico();
        } else if ("extraer_factura".equals(toolName)) {
            return crearToolSchemaFactura();
        } else {
            return crearToolSchemaBotella();
        }
    }

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

private Map<String, Object> crearToolSchemaFactura() {
        Map<String, Object> productoProperties = Map.of(
                "nombre", Map.of(
                        "type", "string",
                        "description", "Descripción del producto visible en la tabla"
                ),
                "marca", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Marca del producto si es visible, null si no aplica"
                ),
                "tipo", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Tipo de bebida (cerveza, gaseosa, agua, etc.) si es identificable, null si no aplica"
                ),
                "cantidad", Map.of(
                        "type", List.of("number", "null"),
                        "description", "Cantidad de unidades del producto"
                ),
                "precioUnitario", Map.of(
                        "type", List.of("number", "null"),
                        "description", "Precio unitario del producto"
                ),
                "subtotal", Map.of(
                        "type", List.of("number", "null"),
                        "description", "Subtotal (cantidad × precio unitario)"
                ),
                "nivelConfianza", Map.of(
                        "type", "integer",
                        "description", "Nivel de confianza del OCR para este producto (0-100). 100 = completamente legible, 50 = parcialmente legible, 0 = ilegible. Ser honesto con la legibilidad."
                )
        );
        
        Map<String, Object> productoSchema = Map.of(
                "type", "object",
                "properties", productoProperties,
                "required", List.of("nombre", "nivelConfianza")
        );
        
        Map<String, Object> properties = Map.of(
                "numeroFactura", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Número de factura visible (ej: 043-003-008450776)"
                ),
                "razonSocialProveedor", Map.of(
                        "type", "string",
                        "description", "Razón social del proveedor emisor"
                ),
                "rucProveedor", Map.of(
                        "type", List.of("string", "null"),
                        "description", "RUC del proveedor (13 dígitos)"
                ),
                "fechaFactura", Map.of(
                        "type", List.of("string", "null"),
                        "description", "Fecha de emisión que aparece junto a 'FECHA Y HORA DE AUTORIZACIÓN' (formato: DD/MM/YYYY o DD/MM/YYYY HH:MM:SS). Extraer SOLO la fecha, no la hora."
                ),
                "totalFactura", Map.of(
                        "type", List.of("number", "null"),
                        "description", "Monto total visible en la parte inferior derecha junto a 'VALOR TOTAL' (solo el número, sin símbolo $)"
                ),
                "productos", Map.of(
                        "type", "array",
                        "description", "Lista de productos de la tabla central de la factura",
                        "items", productoSchema
                ),
                "nivelConfianzaGeneral", Map.of(
                        "type", "integer",
                        "description", "Nivel de confianza general del OCR para toda la factura (0-100). Evalúa la calidad general de la imagen y legibilidad."
                )
        );

        Map<String, Object> inputSchema = Map.of(
                "type", "object",
                "properties", properties,
                "required", List.of("razonSocialProveedor", "nivelConfianzaGeneral")
        );

        return Map.of(
                "name", "extraer_factura",
                "description", "Extrae datos estructurados de una factura ecuatoriana. Busca la fecha junto a 'FECHA Y HORA DE AUTORIZACIÓN', el total en 'VALOR TOTAL', y los productos en la tabla central. IMPORTANTE: Evalúa honestamente la legibilidad y asigna niveles de confianza realistas.",
                "input_schema", inputSchema
        );
    }

private RespuestaClaudeDto<String> extraerTextoYTokensDeRespuesta(String respuestaRaw) {
        try {
            JsonNode raiz = objectMapper.readTree(respuestaRaw);
            JsonNode contenido = raiz.path("content");
            
            String texto = null;
            if (contenido.isArray() && !contenido.isEmpty()) {
                JsonNode primerBloque = contenido.get(0);
                
                if ("tool_use".equals(primerBloque.path("type").asText())) {
                    JsonNode input = primerBloque.path("input");
                    texto = objectMapper.writeValueAsString(input);
                } else {
                    texto = primerBloque.path("text").asText();
                    
                    texto = texto.trim();
                    if (texto.startsWith("```json")) {
                        texto = texto.substring(7);
                    } else if (texto.startsWith("```")) {
                        texto = texto.substring(3);
                    }
                    if (texto.endsWith("```")) {
                        texto = texto.substring(0, texto.length() - 3);
                    }
                    texto = texto.trim();
                }
            }
            
            if (texto == null) {
                throw new ServicioNoDisponibleException(
                        "La respuesta de Claude no contiene bloques de contenido válidos");
            }
            
            JsonNode usage = raiz.path("usage");
            Long tokensInput = usage.path("input_tokens").asLong(0L);
            Long tokensOutput = usage.path("output_tokens").asLong(0L);
            Long tokensCacheRead = usage.path("cache_read_input_tokens").asLong(0L);
            Long tokensCacheWrite = usage.path("cache_creation_input_tokens").asLong(0L);
            
            return RespuestaClaudeDto.<String>builder()
                    .resultado(texto)
                    .tokensInput(tokensInput)
                    .tokensOutput(tokensOutput)
                    .tokensCacheRead(tokensCacheRead)
                    .tokensCacheWrite(tokensCacheWrite)
                    .build();
                    
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "No se pudo parsear la respuesta de la API de Claude: " + e.getMessage());
        }
    }

private String resolverMediaType(String formato) {
        if (formato == null) {
            return "image/jpeg";
        }
        return switch (formato.toUpperCase()) {
            case "PNG"  -> "image/png";
            case "WEBP" -> "image/webp";
            case "GIF"  -> "image/gif";
            default     -> "image/jpeg";
        };
    }
}
