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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.SecuenciaCodigoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.SecuenciaCodigoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ISecuenciaCodigoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/secuencias-codigo")
public class SecuenciaCodigoController {

	private final ISecuenciaCodigoUseCase secuenciaCodigoUseCase;
	private final ISecuenciaCodigoDtoMapper mapper;

	public SecuenciaCodigoController(ISecuenciaCodigoUseCase secuenciaCodigoUseCase, ISecuenciaCodigoDtoMapper mapper) {
		this.secuenciaCodigoUseCase = secuenciaCodigoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public SecuenciaCodigoResponseDto guardar(@Valid @RequestBody SecuenciaCodigoRequestDto requestDto) {
		return mapper.toResponseDto(secuenciaCodigoUseCase.guardar(mapper.toDomain(requestDto)));
	}
	
	@GetMapping
	public List<SecuenciaCodigoResponseDto> listarTodo() { 
		return secuenciaCodigoUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") int idSecuenciaCodigo) {
		secuenciaCodigoUseCase.eliminar(idSecuenciaCodigo);
		return ResponseEntity.noContent().build();
	}

}