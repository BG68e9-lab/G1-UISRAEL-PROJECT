package com.uisrael.drinkhouse.presentacion.controladores;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAjusteInventarioAuditoriaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.response.AjusteAuditoriaResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IAjusteAuditoriaDtoMapper;

/**
 * Controlador REST para consultar registros de auditoría de ajustes de inventario.
 * 
 * Expone endpoints para recuperar información de auditoría asociada a movimientos
 * de inventario, incluyendo detalles de autorización, cambios en cantidades, y
 * metadatos de la operación.
 * 
 * Nota: Requiere rol ADMIN o BODEGUERO para acceder a los registros de auditoría
 * (autorización será implementada cuando Spring Security esté configurado).
 */
@RestController
@RequestMapping("/api/v1/ajustes-auditoria")
@Tag(name = "Auditoría de Inventario", description = "Endpoints para consultar registros de auditoría de movimientos de inventario con trazabilidad completa")
public class AjusteInventarioAuditoriaController {

	private static final Logger logger = LoggerFactory.getLogger(AjusteInventarioAuditoriaController.class);

	private final IAjusteInventarioAuditoriaUseCase auditUseCase;
	private final IAjusteAuditoriaDtoMapper mapper;

	/**
	 * Constructor con inyección de dependencias.
	 * 
	 * @param auditUseCase caso de uso para operaciones de auditoría
	 * @param mapper       mapper para convertir entre dominio y DTO
	 */
	public AjusteInventarioAuditoriaController(
			IAjusteInventarioAuditoriaUseCase auditUseCase,
			IAjusteAuditoriaDtoMapper mapper) {
		this.auditUseCase = auditUseCase;
		this.mapper = mapper;
	}

	/**
	 * Busca el registro de auditoría asociado a un movimiento de inventario.
	 * 
	 * @param movimientoId ID del movimiento de inventario
	 * @return ResponseEntity con HTTP 200 y el registro de auditoría completo
	 * @throws RecursoNoEncontradoException si no se encuentra el registro (HTTP 404)
	 */
	@Operation(
		summary = "Consultar auditoría por ID de movimiento",
		description = "Recupera el registro de auditoría completo asociado a un movimiento de inventario específico. "
			+ "El registro incluye detalles de autorización (usuario autorizado y ejecutor), justificación, "
			+ "cambios de stock (cantidad anterior, ajuste, cantidad posterior), y metadatos de trazabilidad "
			+ "(IP, sesión, fecha/hora). Este endpoint permite rastrear completamente cualquier cambio de inventario "
			+ "con fines de auditoría y cumplimiento."
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "200",
			description = "Registro de auditoría encontrado exitosamente",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = AjusteAuditoriaResponseDto.class),
				examples = @ExampleObject(
					name = "Ejemplo de registro de auditoría",
					value = """
						{
							"ajusteId": 789,
							"movimientoId": 1245,
							"productoId": 101,
							"productoNombre": "Coca Cola 2L",
							"loteId": 50,
							"loteCodigo": "LOTE-2024-01",
							"tipoMovimiento": "AJUSTE_POSITIVO",
							"cantidadAnterior": 100.00,
							"ajuste": 50.00,
							"cantidadPosterior": 150.00,
							"usuarioAutorizado": "jperez",
							"usuarioEjecutor": "mjohnson",
							"justificacion": "Ajuste por inventario físico realizado el 15/01/2024",
							"fechaHora": "2024-01-15T14:30:00-05:00",
							"direccionIp": "192.168.1.100",
							"sessionId": "AB123456789",
							"ventaId": null
						}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "No se encontró registro de auditoría para el movimiento especificado",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Registro no encontrado",
					value = """
						{
							"timestamp": "2024-01-15T14:30:00-05:00",
							"status": 404,
							"error": "Not Found",
							"message": "No se encontró registro de auditoría para el movimiento 9999",
							"path": "/api/v1/ajustes-auditoria/movimiento/9999"
						}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "403",
			description = "Acceso denegado - el usuario no tiene rol ADMIN o BODEGUERO",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Error de autorización",
					value = """
						{
							"timestamp": "2024-01-15T14:30:00-05:00",
							"status": 403,
							"error": "Forbidden",
							"message": "Acceso denegado: se requiere rol ADMIN o BODEGUERO",
							"path": "/api/v1/ajustes-auditoria/movimiento/1245"
						}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "500",
			description = "Error interno del servidor",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Error interno",
					value = """
						{
							"timestamp": "2024-01-15T14:30:00-05:00",
							"status": 500,
							"error": "Internal Server Error",
							"message": "Error al consultar el registro de auditoría",
							"path": "/api/v1/ajustes-auditoria/movimiento/1245"
						}
					"""
				)
			)
		)
	})
	@GetMapping("/movimiento/{movimientoId}")
	public ResponseEntity<AjusteAuditoriaResponseDto> buscarPorMovimiento(
			@Parameter(
				description = "ID del movimiento de inventario para el cual se desea consultar el registro de auditoría",
				required = true,
				example = "1245"
			)
			@PathVariable Long movimientoId) {
		
		logger.info("Recibida solicitud de consulta de auditoría para movimiento ID: {}", movimientoId);
		
		AjusteAuditoriaResponseDto response = mapper.toResponseDto(
				auditUseCase.buscarPorMovimiento(movimientoId));
		
		logger.info("Auditoría consultada exitosamente para movimiento ID: {}", movimientoId);
		
		return ResponseEntity.ok(response);
	}
}
