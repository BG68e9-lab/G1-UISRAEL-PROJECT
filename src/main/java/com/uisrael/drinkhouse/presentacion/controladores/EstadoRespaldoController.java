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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoRespaldoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.EstadoRespaldoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.EstadoRespaldoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IEstadoRespaldoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estados-respaldo")
public class EstadoRespaldoController {

	private final IEstadoRespaldoUseCase estadoRespaldoUseCase;
	private final IEstadoRespaldoDtoMapper mapper;

	public EstadoRespaldoController(IEstadoRespaldoUseCase estadoRespaldoUseCase, IEstadoRespaldoDtoMapper mapper) {
		this.estadoRespaldoUseCase = estadoRespaldoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoRespaldoResponseDto guardar(@Valid @RequestBody EstadoRespaldoRequestDto requestDto) {
		return mapper.toResponseDto(estadoRespaldoUseCase.guardar(mapper.toDomain(requestDto)));
	}
	
	@GetMapping
	public List<EstadoRespaldoResponseDto> listarTodo() { 
		return estadoRespaldoUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int idEstadoRespaldo) {
		estadoRespaldoUseCase.eliminar(idEstadoRespaldo);
		return ResponseEntity.noContent().build();
	}

}