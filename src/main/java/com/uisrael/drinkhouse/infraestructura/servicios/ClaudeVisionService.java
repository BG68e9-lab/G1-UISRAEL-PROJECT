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
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoBotellaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;

/**
 * Servicio de infraestructura que encapsula la comunicación con la API de Claude (Anthropic).
 * Proporciona métodos para identificar botellas y extraer datos de facturas a partir de imágenes.
 */
@Service
public class ClaudeVisionService {

    private static final String PROMPT_BOTELLA = """
            Analiza la imagen de esta botella de bebida alcohólica. Responde ÚNICAMENTE con un JSON válido con esta estructura exacta, sin texto adicional:
            {
              "nombre": "nombre del producto",
              "marca": "marca de la bebida",
              "tipo": "whisky|ron|vodka|gin|tequila|vino|cerveza|pisco|otro",
              "presentacion": "750ml|1L|500ml|375ml|otros",
              "graduacionAlcohol": "número en porcentaje o null si no es visible",
              "reconocido": true|false
            }
            Si no puedes identificar claramente el producto, pon reconocido: false y los campos que no puedas determinar como null.
            """;

    private static final String PROMPT_FACTURA = """
            Analiza esta imagen de factura/documento de compra. Responde ÚNICAMENTE con un JSON válido con esta estructura exacta, sin texto adicional:
            {
              "rucProveedor": "RUC del emisor o null",
              "razonSocialProveedor": "nombre del proveedor o null",
              "fechaFactura": "fecha en formato YYYY-MM-DD o null",
              "numeroFactura": "número de factura o null",
              "productos": [
                {
                  "nombre": "nombre del producto",
                  "marca": "marca o null",
                  "tipo": "tipo de bebida o null",
                  "cantidad": número,
                  "precioUnitario": número,
                  "subtotal": número
                }
              ],
              "totalFactura": número o null
            }
            Si no puedes leer algún campo con claridad, usa null.
            """;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${anthropic.api.key}")
    private String apiKey;

    @Value("${anthropic.model:claude-3-5-sonnet-20241022}")
    private String modelo;

    @Value("${anthropic.max-tokens:300}")
    private int maxTokens;

    public ClaudeVisionService(
            @Value("${anthropic.api.url:https://api.anthropic.com/v1/messages}") String apiUrl,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
        String respuestaJson = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_BOTELLA);
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
        String respuestaJson = llamarClaudeConImagen(imagenBase64, formatoImagen, PROMPT_FACTURA);
        try {
            return objectMapper.readValue(respuestaJson, ResultadoFacturaDto.class);
        } catch (JsonProcessingException e) {
            throw new ServicioNoDisponibleException(
                    "La respuesta de Claude para extracción de factura no es un JSON válido: " + e.getMessage());
        }
    }

    /**
     * Realiza la llamada HTTP a la API de Claude con una imagen y un prompt.
     *
     * @param imagenBase64  imagen en base64
     * @param formatoImagen formato de la imagen
     * @param prompt        instrucción de texto para Claude
     * @return texto plano devuelto por Claude (se espera que sea JSON)
     */
    private String llamarClaudeConImagen(String imagenBase64, String formatoImagen, String prompt) {
        String mediaType = resolverMediaType(formatoImagen);

        // Construir el cuerpo de la solicitud según la especificación de la API de Claude
        Map<String, Object> fuenteImagen = Map.of(
                "type", "base64",
                "media_type", mediaType,
                "data", imagenBase64
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

        Map<String, Object> cuerpoSolicitud = Map.of(
                "model", modelo,
                "max_tokens", maxTokens,
                "messages", List.of(mensaje)
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
     * Extrae el texto del primer bloque de contenido de la respuesta de Claude.
     *
     * @param respuestaRaw JSON completo devuelto por la API de Claude
     * @return texto del primer bloque content[0].text
     */
    private String extraerTextoDeRespuesta(String respuestaRaw) {
        try {
            JsonNode raiz = objectMapper.readTree(respuestaRaw);
            JsonNode contenido = raiz.path("content");
            if (contenido.isArray() && !contenido.isEmpty()) {
                return contenido.get(0).path("text").asText();
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
