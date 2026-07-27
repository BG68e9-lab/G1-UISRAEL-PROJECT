package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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

	public MovimientoInventarioController(IMovimientoInventarioUseCase movimientoUseCase,
			IMovimientoInventarioDtoMapper mapper) {
		this.movimientoUseCase = movimientoUseCase;
		this.mapper = mapper;
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
}
