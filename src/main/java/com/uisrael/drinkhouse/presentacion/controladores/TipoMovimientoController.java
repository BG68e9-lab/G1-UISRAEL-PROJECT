package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoMovimientoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.TipoMovimientoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.TipoMovimientoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ITipoMovimientoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tipos-movimiento")
public class TipoMovimientoController {

	private final ITipoMovimientoUseCase tipoMovimientoUseCase;
	private final ITipoMovimientoDtoMapper mapper;

	public TipoMovimientoController(ITipoMovimientoUseCase tipoMovimientoUseCase, ITipoMovimientoDtoMapper mapper) {
		this.tipoMovimientoUseCase = tipoMovimientoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<TipoMovimientoResponseDto> crear(@Valid @RequestBody TipoMovimientoRequestDto requestDto) {
		TipoMovimientoResponseDto response = mapper.toResponseDto(
				tipoMovimientoUseCase.crearTipoMovimiento(mapper.toDomain(requestDto)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping
	public ResponseEntity<List<TipoMovimientoResponseDto>> listar() {
		List<TipoMovimientoResponseDto> lista = tipoMovimientoUseCase.listarTodos()
				.stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TipoMovimientoResponseDto> buscarPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(mapper.toResponseDto(tipoMovimientoUseCase.buscarPorId(id)));
	}
}
