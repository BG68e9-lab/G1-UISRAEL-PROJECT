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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.MovimientoInventarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IMovimientoInventarioDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movimientos") // Puedes cambiarlo a "/api/movimientos-inventario" si prefieres
public class MovimientoInventarioController {

	private final IMovimientoInventarioUseCase movimientoUseCase;
	private final IMovimientoInventarioDtoMapper mapper;

	public MovimientoInventarioController(IMovimientoInventarioUseCase movimientoUseCase, IMovimientoInventarioDtoMapper mapper) {
		this.movimientoUseCase = movimientoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MovimientoInventarioResponseDto guardar(@Valid @RequestBody MovimientoInventarioRequestDto requestDto) {
		return mapper.toResponseDto(movimientoUseCase.guardar(mapper.toDomain(requestDto)));
	}
	
	@GetMapping
	public List<MovimientoInventarioResponseDto> listarTodo() { 
		return movimientoUseCase.listarTodo().stream().map(mapper::toResponseDto).toList();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int idMovimiento) {
		movimientoUseCase.eliminar(idMovimiento);
		return ResponseEntity.noContent().build();
	}
}