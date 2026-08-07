package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.infraestructura.servicios.ExportacionService;
import com.uisrael.drinkhouse.presentacion.dto.request.MovimientoInventarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IMovimientoInventarioDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/movimientos")
@Tag(name = "Movimientos de Inventario", description = "Endpoints para gestionar movimientos de inventario (entradas, salidas, ajustes) con auditoría y validación de stock")
public class MovimientoInventarioController {

	private static final Logger logger = LoggerFactory.getLogger(MovimientoInventarioController.class);

	private final IMovimientoInventarioUseCase movimientoUseCase;
	private final IMovimientoInventarioDtoMapper mapper;
	private final ExportacionService exportacionService;

	public MovimientoInventarioController(
			IMovimientoInventarioUseCase movimientoUseCase,
			IMovimientoInventarioDtoMapper mapper,
			ExportacionService exportacionService) {
		this.movimientoUseCase = movimientoUseCase;
		this.mapper = mapper;
		this.exportacionService = exportacionService;
	}

@PostMapping
	@Transactional
	public ResponseEntity<MovimientoInventarioResponseDto> registrar(
			@Valid @RequestBody MovimientoInventarioRequestDto requestDto) {

		boolean requiereAuditoria = requestDto.getCantidadAnterior() != null 
				&& requestDto.getAjuste() != null
				&& requestDto.getCantidadPosterior() != null
				&& requestDto.getJustificacion() != null
				&& requestDto.getUsuarioAutorizado() != null;

		if (requiereAuditoria) {
			logger.info("Movimiento con auditoría detectado");
			throw new ReglaNegocioException("Para movimientos con auditoría use POST /api/v1/movimientos/con-auditoria");
		}

		MovimientoInventario dominio = mapper.toDomain(requestDto);
		MovimientoInventario guardado = movimientoUseCase.registrar(
				requestDto.getProductoId(),
				requestDto.getLoteId(),
				requestDto.getTipoMovimientoId(),
				dominio);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(mapper.toResponseDto(guardado));
	}

@Operation(
		summary = "Crear movimiento de inventario con auditoría",
		description = "Registra un movimiento de inventario (ENTRADA, SALIDA, AJUSTE) con autenticación secundaria y trazabilidad completa. "
			+ "La operación valida el cálculo de stock (cantidad_anterior + ajuste = cantidad_posterior), actualiza el inventario y "
			+ "crea un registro de auditoría completo con detalles de autorización, justificación y metadatos.",
		security = @SecurityRequirement(name = "X-Secondary-Auth")
	)
	@ApiResponses({
		@ApiResponse(
			responseCode = "201",
			description = "Movimiento creado exitosamente con registro de auditoría",
			content = @Content(
				mediaType = "application/json",
				schema = @Schema(implementation = MovimientoInventarioResponseDto.class),
				examples = @ExampleObject(
					name = "Ejemplo de respuesta exitosa",
					value = """
						{
							"movimientoId": 1245,
							"productoId": 101,
							"productoNombre": "Coca Cola 2L",
							"loteId": 50,
							"loteCodigo": "LOTE-2024-01",
							"tipoMovimiento": "AJUSTE_POSITIVO",
							"cantidad": 50.00,
							"cantidadAnterior": 100.00,
							"ajuste": 50.00,
							"cantidadPosterior": 150.00,
							"precioUnitario": 2.50,
							"ventaId": null,
							"fechaHora": "2024-01-15T14:30:00-05:00"
						}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "400",
			description = "Error de validación - datos inválidos o cálculo de stock incorrecto",
			content = @Content(
				mediaType = "application/json",
				examples = {
					@ExampleObject(
						name = "Validación de stock fallida",
						value = """
							{
								"timestamp": "2024-01-15T14:30:00-05:00",
								"status": 400,
								"error": "Bad Request",
								"message": "Validación de stock fallida: cantidad_anterior (100) + ajuste (50) debe ser igual a cantidad_posterior (160)",
								"path": "/api/v1/movimientos/con-auditoria"
							}
						"""
					),
					@ExampleObject(
						name = "Justificación inválida",
						value = """
							{
								"timestamp": "2024-01-15T14:30:00-05:00",
								"status": 400,
								"error": "Bad Request",
								"message": "Justificación debe tener entre 10 y 500 caracteres",
								"path": "/api/v1/movimientos/con-auditoria"
							}
						"""
					),
					@ExampleObject(
						name = "Stock actual no coincide",
						value = """
							{
								"timestamp": "2024-01-15T14:30:00-05:00",
								"status": 400,
								"error": "Bad Request",
								"message": "Stock actual del producto no coincide: esperado 100, encontrado 95",
								"path": "/api/v1/movimientos/con-auditoria"
							}
						"""
					)
				}
			)
		),
		@ApiResponse(
			responseCode = "401",
			description = "Autenticación secundaria inválida - código de acceso no válido o expirado",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Error de autenticación secundaria",
					value = """
						{
							"timestamp": "2024-01-15T14:30:00-05:00",
							"status": 401,
							"error": "Unauthorized",
							"message": "Autenticación secundaria inválida",
							"path": "/api/v1/movimientos/con-auditoria"
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
							"path": "/api/v1/movimientos/con-auditoria"
						}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "404",
			description = "Recurso no encontrado - producto, lote o venta no existe",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Producto no encontrado",
					value = """
						{
							"timestamp": "2024-01-15T14:30:00-05:00",
							"status": 404,
							"error": "Not Found",
							"message": "Producto con ID 999 no encontrado",
							"path": "/api/v1/movimientos/con-auditoria"
						}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "409",
			description = "Conflicto de concurrencia - el stock del producto fue modificado por otra operación",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Error de concurrencia",
					value = """
						{
							"timestamp": "2024-01-15T14:30:00-05:00",
							"status": 409,
							"error": "Conflict",
							"message": "Conflicto de concurrencia: el stock del producto fue modificado por otra operación. Intente nuevamente",
							"path": "/api/v1/movimientos/con-auditoria"
						}
					"""
				)
			)
		),
		@ApiResponse(
			responseCode = "500",
			description = "Error interno del servidor - fallo en la transacción o error inesperado",
			content = @Content(
				mediaType = "application/json",
				examples = @ExampleObject(
					name = "Error interno",
					value = """
						{
							"timestamp": "2024-01-15T14:30:00-05:00",
							"status": 500,
							"error": "Internal Server Error",
							"message": "Error al procesar la transacción de inventario",
							"path": "/api/v1/movimientos/con-auditoria"
						}
					"""
				)
			)
		)
	})
	@PostMapping("/con-auditoria")
	@Transactional
	public ResponseEntity<MovimientoInventarioResponseDto> crearMovimientoConAuditoria(
			@Parameter(
				description = "Datos completos del movimiento de inventario con campos de validación de stock y auditoría",
				required = true,
				content = @Content(
					mediaType = "application/json",
					schema = @Schema(implementation = MovimientoInventarioRequestDto.class),
					examples = @ExampleObject(
						name = "Ejemplo de solicitud",
						value = """
							{
								"productoId": 101,
								"loteId": 50,
								"tipoMovimientoId": 3,
								"cantidad": 50.00,
								"cantidadAnterior": 100.00,
								"ajuste": 50.00,
								"cantidadPosterior": 150.00,
								"precioUnitario": 2.50,
								"justificacion": "Ajuste por inventario físico realizado el 15/01/2024",
								"usuarioAutorizado": "jperez",
								"direccionIp": "192.168.1.100",
								"sessionId": "AB123456789",
								"ventaId": null
							}
						"""
					)
				)
			)
			@Valid @RequestBody MovimientoInventarioRequestDto requestDto,
			
			@Parameter(
				description = "Token de autenticación secundaria para validar operaciones sensibles de inventario. "
					+ "Debe ser un código de acceso válido, no expirado y no utilizado previamente.",
				required = true,
				example = "SEC-AUTH-ABC123XYZ"
			)
			@RequestHeader("X-Secondary-Auth") String secondaryAuthToken,
			
			HttpServletRequest request) {

		logger.info("Recibida solicitud de creación de movimiento con auditoría - Producto ID: {}, Tipo movimiento ID: {}", 
				requestDto.getProductoId(), requestDto.getTipoMovimientoId());

		String justificacionTrimmed = requestDto.getJustificacion() != null 
				? requestDto.getJustificacion().trim() 
				: "";

		String usuarioEjecutor = request.getUserPrincipal() != null 
				? request.getUserPrincipal().getName() 
				: requestDto.getUsuarioAutorizado();

		String direccionIp = extractIpAddress(request);

		HttpSession session = request.getSession(false);
		String sessionId = session != null ? session.getId() : null;

		logger.info("Información de solicitud - Usuario ejecutor: {}, Usuario autorizado: {}, IP: {}, Session: {}", 
				usuarioEjecutor, requestDto.getUsuarioAutorizado(), direccionIp, sessionId);

		MovimientoInventario dominio = mapper.toDomainWithAuditFields(requestDto);

		MovimientoInventario guardado = movimientoUseCase.crearMovimientoConAuditoria(
				dominio,
				requestDto.getUsuarioAutorizado(),
				usuarioEjecutor,
				justificacionTrimmed,
				direccionIp,
				sessionId);

		logger.info("Movimiento con auditoría creado exitosamente - Movimiento ID: {}, Producto ID: {}", 
				guardado.getMovimientoId(), guardado.getProductoId());

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(mapper.toResponseDto(guardado));
	}

private String extractIpAddress(HttpServletRequest request) {
		String ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		if (ipAddress != null && ipAddress.contains(",")) {
			ipAddress = ipAddress.split(",")[0].trim();
		}
		return ipAddress;
	}

@GetMapping
	public ResponseEntity<List<MovimientoInventarioResponseDto>> listarTodos() {
		List<MovimientoInventarioResponseDto> lista = movimientoUseCase.listarTodos()
				.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(lista);
	}

@GetMapping("/{id}")
	public ResponseEntity<MovimientoInventarioResponseDto> buscarPorId(@PathVariable Long id) {
		MovimientoInventario movimiento = movimientoUseCase.buscarPorId(id);
		return ResponseEntity.ok(mapper.toResponseDto(movimiento));
	}

@GetMapping("/tipo/{tipo}")
	public ResponseEntity<List<MovimientoInventarioResponseDto>> listarPorTipo(
			@PathVariable String tipo) {
		List<MovimientoInventarioResponseDto> lista = movimientoUseCase.buscarPorTipo(tipo)
				.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(lista);
	}

@GetMapping("/lote/{loteId}")
	public ResponseEntity<List<MovimientoInventarioResponseDto>> listarPorLote(
			@PathVariable Long loteId) {
		List<MovimientoInventarioResponseDto> lista = movimientoUseCase.buscarPorLote(loteId)
				.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(lista);
	}

@GetMapping("/producto/{productoId}")
	public ResponseEntity<List<MovimientoInventarioResponseDto>> listarPorProducto(
			@PathVariable Long productoId,
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {

		List<MovimientoInventarioResponseDto> lista =
				movimientoUseCase.buscarPorProductoConFiltros(productoId, tipo, desde, hasta)
						.stream()
						.map(mapper::toResponseDto)
						.toList();

		return ResponseEntity.ok(lista);
	}

@GetMapping("/export/excel")
	public ResponseEntity<byte[]> exportarAExcel(
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {

		List<MovimientoInventarioResponseDto> movimientos = movimientoUseCase.listarTodos()
				.stream()
				.map(mapper::toResponseDto)
				.toList();

		byte[] excelBytes = exportacionService.exportarMovimientosAExcel(movimientos);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "movimientos_inventario.xlsx");

		return ResponseEntity.ok()
				.headers(headers)
				.body(excelBytes);
	}

@GetMapping("/export/pdf")
	public ResponseEntity<byte[]> exportarAPdf(
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {

		List<MovimientoInventarioResponseDto> movimientos = movimientoUseCase.listarTodos()
				.stream()
				.map(mapper::toResponseDto)
				.toList();

		byte[] pdfBytes = exportacionService.exportarMovimientosAPdf(movimientos);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "movimientos_inventario.pdf");

		return ResponseEntity.ok()
				.headers(headers)
				.body(pdfBytes);
	}
}
