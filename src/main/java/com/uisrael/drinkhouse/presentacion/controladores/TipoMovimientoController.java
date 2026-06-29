package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoMovimientoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.TipoMovimientoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.TipoMovimientoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ITipoMovimientoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tipos-movimiento")
public class TipoMovimientoController {

	private final ITipoMovimientoUseCase tipoMovimientoUseCase;
	private final ITipoMovimientoDtoMapper mapper;

	public TipoMovimientoController(ITipoMovimientoUseCase tipoMovimientoUseCase, ITipoMovimientoDtoMapper mapper) {
		this.tipoMovimientoUseCase = tipoMovimientoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TipoMovimientoResponseDto guardar(@Valid @RequestBody TipoMovimientoRequestDto requestDto) {
		return mapper.toResponseDto(tipoMovimientoUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<TipoMovimientoResponseDto> listarTodo() {
		return tipoMovimientoUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int idTipoMovimiento) {
		tipoMovimientoUseCase.eliminar(idTipoMovimiento);
		return ResponseEntity.noContent().build();
	}

}