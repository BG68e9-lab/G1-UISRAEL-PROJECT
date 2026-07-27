package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IIdentificacionIaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;
import com.uisrael.drinkhouse.infraestructura.servicios.ClaudeVisionService;
import com.uisrael.drinkhouse.presentacion.dto.request.IdentificacionIaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.IdentificacionIaResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoBotellaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IIdentificacionIaDtoMapper;

import jakarta.validation.Valid;

/**
 * Controlador REST para el módulo de Identificación de Productos mediante IA.
 * Base de URL: /api/v1/ia
 */
@RestController
@RequestMapping("/api/v1/ia")
public class IdentificacionIaController {

    private final IIdentificacionIaUseCase identificacionUseCase;
    private final IIdentificacionIaDtoMapper dtoMapper;
    private final ClaudeVisionService claudeVisionService;

    public IdentificacionIaController(
            IIdentificacionIaUseCase identificacionUseCase,
            IIdentificacionIaDtoMapper dtoMapper,
            ClaudeVisionService claudeVisionService) {
        this.identificacionUseCase = identificacionUseCase;
        this.dtoMapper = dtoMapper;
        this.claudeVisionService = claudeVisionService;
    }

    /**
     * Identifica un producto mediante IA a partir de una imagen en base64.
     * El campo {@code tipoIdentificacion} del request determina si se analiza
     * una botella ("BOTELLA") o una factura ("FACTURA").
     *
     * POST /api/v1/ia/identificar
     * Código de respuesta: 201 Created
     * Errores posibles: 400 (formato/tipo inválido), 404 (producto/negocio no existe),
     *                   429 (cuota agotada), 503 (Claude no disponible)
     */
    @PostMapping("/identificar")
    public ResponseEntity<IdentificacionIaResponseDto> identificarProducto(
            @Valid @RequestBody IdentificacionIaRequestDto solicitud) {

        // Ejecutar el caso de uso — persiste la identificación e incrementa el consumo
        IdentificacionIa resultado = identificacionUseCase.identificarProducto(
                solicitud.getImagenBase64(),
                solicitud.getFormatoImagen(),
                solicitud.getProductoId(),
                solicitud.getNegocioId(),
                solicitud.getTipoIdentificacion());

        // Construir la respuesta base desde el mapper
        IdentificacionIaResponseDto respuesta = dtoMapper.aResponseDto(resultado);

        // Agregar el resultado estructurado de Claude según el tipo de identificación
        enricherConResultadoClaude(respuesta, solicitud);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /**
     * Consulta el historial de identificaciones IA con filtros opcionales.
     *
     * GET /api/v1/ia/historial
     * Parámetros opcionales: productoId, desde, hasta
     * Código de respuesta: 200 OK
     */
    @GetMapping("/historial")
    public ResponseEntity<List<IdentificacionIaResponseDto>> consultarHistorial(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) OffsetDateTime desde,
            @RequestParam(required = false) OffsetDateTime hasta) {

        List<IdentificacionIa> historial = identificacionUseCase.consultarHistorial(
                productoId, desde, hasta);

        List<IdentificacionIaResponseDto> respuesta = dtoMapper.aListaResponseDto(historial);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Enriquece el DTO de respuesta con el resultado estructurado de Claude
     * (ResultadoBotellaDto o ResultadoFacturaDto) y el tipo de identificación.
     * Se llama a Claude nuevamente para obtener el objeto estructurado completo,
     * ya que el caso de uso solo persiste los campos básicos en la entidad de dominio.
     *
     * @param respuesta DTO de respuesta a enriquecer
     * @param solicitud solicitud original del cliente
     */
    private void enricherConResultadoClaude(
            IdentificacionIaResponseDto respuesta,
            IdentificacionIaRequestDto solicitud) {

        respuesta.setTipoIdentificacion(solicitud.getTipoIdentificacion().toUpperCase());

        if ("BOTELLA".equalsIgnoreCase(solicitud.getTipoIdentificacion())) {
            ResultadoBotellaDto resultadoBotella = claudeVisionService.identificarBotella(
                    solicitud.getImagenBase64(), solicitud.getFormatoImagen());
            respuesta.setResultadoBotella(resultadoBotella);
            respuesta.setReconocido(resultadoBotella.getReconocido());
        } else if ("FACTURA".equalsIgnoreCase(solicitud.getTipoIdentificacion())) {
            ResultadoFacturaDto resultadoFactura = claudeVisionService.extraerFactura(
                    solicitud.getImagenBase64(), solicitud.getFormatoImagen());
            respuesta.setResultadoFactura(resultadoFactura);
            respuesta.setReconocido(resultadoFactura.getNumeroFactura() != null);
        }
    }
}
