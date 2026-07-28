package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.infraestructura.servicios.ExportacionService;
import com.uisrael.drinkhouse.presentacion.dto.request.MovimientoInventarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IMovimientoInventarioDtoMapper;

import jakarta.validation.Valid;

/**
 * Controlador REST para el módulo de movimientos de inventario.
 * Base URL: /api/v1/movimientos
 */
@RestController
@RequestMapping("/api/v1/movimientos")
public class MovimientoInventarioController {

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

	/**
	 * POST /api/v1/movimientos
	 * Registra un nuevo movimiento de inventario (ENTRADA, SALIDA o AJUSTE).
	 *
	 * @param requestDto datos del movimiento a registrar
	 * @return el movimiento creado con código generado, HTTP 201
	 */
	@PostMapping
	public ResponseEntity<MovimientoInventarioResponseDto> registrar(
			@Valid @RequestBody MovimientoInventarioRequestDto requestDto) {

		MovimientoInventario dominio = mapper.toDomain(requestDto);
		MovimientoInventario guardado = movimientoUseCase.registrar(
				requestDto.getProductoId(),
				requestDto.getLoteId(),
				requestDto.getTipoMovimientoId(),
				dominio);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(mapper.toResponseDto(guardado));
	}

	/**
	 * GET /api/v1/movimientos
	 * Lista todos los movimientos del sistema ordenados por fecha descendente.
	 *
	 * @return lista de todos los movimientos, HTTP 200
	 */
	@GetMapping
	public ResponseEntity<List<MovimientoInventarioResponseDto>> listarTodos() {
		List<MovimientoInventarioResponseDto> lista = movimientoUseCase.listarTodos()
				.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(lista);
	}

	/**
	 * GET /api/v1/movimientos/{id}
	 * Busca un movimiento por su ID.
	 *
	 * @param id ID del movimiento
	 * @return movimiento encontrado, HTTP 200
	 */
	@GetMapping("/{id}")
	public ResponseEntity<MovimientoInventarioResponseDto> buscarPorId(@PathVariable Long id) {
		MovimientoInventario movimiento = movimientoUseCase.buscarPorId(id);
		return ResponseEntity.ok(mapper.toResponseDto(movimiento));
	}

	/**
	 * GET /api/v1/movimientos/tipo/{tipo}
	 * Lista movimientos por tipo (ENTRADA, SALIDA, AJUSTE).
	 *
	 * @param tipo código del tipo de movimiento
	 * @return lista de movimientos del tipo especificado, HTTP 200
	 */
	@GetMapping("/tipo/{tipo}")
	public ResponseEntity<List<MovimientoInventarioResponseDto>> listarPorTipo(
			@PathVariable String tipo) {
		List<MovimientoInventarioResponseDto> lista = movimientoUseCase.buscarPorTipo(tipo)
				.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(lista);
	}

	/**
	 * GET /api/v1/movimientos/lote/{loteId}
	 * Lista movimientos asociados a un lote específico.
	 *
	 * @param loteId ID del lote
	 * @return lista de movimientos del lote, HTTP 200
	 */
	@GetMapping("/lote/{loteId}")
	public ResponseEntity<List<MovimientoInventarioResponseDto>> listarPorLote(
			@PathVariable Long loteId) {
		List<MovimientoInventarioResponseDto> lista = movimientoUseCase.buscarPorLote(loteId)
				.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(lista);
	}

	/**
	 * GET /api/v1/movimientos/producto/{productoId}
	 * Lista los movimientos de un producto con filtros opcionales de tipo y rango de fechas.
	 *
	 * @param productoId ID del producto
	 * @param tipo       código del tipo de movimiento (opcional)
	 * @param desde      fecha/hora de inicio del rango ISO-8601 (opcional)
	 * @param hasta      fecha/hora de fin del rango ISO-8601 (opcional)
	 * @return lista de movimientos ordenada por fecha descendente, HTTP 200
	 */
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

	/**
	 * GET /api/v1/movimientos/export/excel
	 * Exporta el historial de movimientos a formato Excel (XLSX).
	 *
	 * @param tipo       código del tipo de movimiento (opcional)
	 * @param desde      fecha/hora de inicio del rango ISO-8601 (opcional)
	 * @param hasta      fecha/hora de fin del rango ISO-8601 (opcional)
	 * @return archivo Excel con los movimientos, HTTP 200
	 */
	@GetMapping("/export/excel")
	public ResponseEntity<byte[]> exportarAExcel(
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {

		// Obtener movimientos con filtros
		List<MovimientoInventarioResponseDto> movimientos = movimientoUseCase.listarTodos()
				.stream()
				.map(mapper::toResponseDto)
				.toList();

		// Generar archivo Excel
		byte[] excelBytes = exportacionService.exportarMovimientosAExcel(movimientos);

		// Preparar headers de respuesta
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
		headers.setContentDispositionFormData("attachment", "movimientos_inventario.xlsx");

		return ResponseEntity.ok()
				.headers(headers)
				.body(excelBytes);
	}

	/**
	 * GET /api/v1/movimientos/export/pdf
	 * Exporta el historial de movimientos a formato PDF.
	 *
	 * @param tipo       código del tipo de movimiento (opcional)
	 * @param desde      fecha/hora de inicio del rango ISO-8601 (opcional)
	 * @param hasta      fecha/hora de fin del rango ISO-8601 (opcional)
	 * @return archivo PDF con los movimientos, HTTP 200
	 */
	@GetMapping("/export/pdf")
	public ResponseEntity<byte[]> exportarAPdf(
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
			@RequestParam(required = false)
			@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {

		// Obtener movimientos con filtros
		List<MovimientoInventarioResponseDto> movimientos = movimientoUseCase.listarTodos()
				.stream()
				.map(mapper::toResponseDto)
				.toList();

		// Generar archivo PDF
		byte[] pdfBytes = exportacionService.exportarMovimientosAPdf(movimientos);

		// Preparar headers de respuesta
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		headers.setContentDispositionFormData("attachment", "movimientos_inventario.pdf");

		return ResponseEntity.ok()
				.headers(headers)
				.body(pdfBytes);
	}
}
