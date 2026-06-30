package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INegocioUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.NegocioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.NegocioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.INegocioDtoMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/negocios")
public class NegocioController {

	private final INegocioUseCase negocioUseCase;
	private final INegocioDtoMapper mapper;

	public NegocioController(INegocioUseCase negocioUseCase, INegocioDtoMapper mapper) {
		this.negocioUseCase = negocioUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public NegocioResponseDto guardar(@Valid @RequestBody NegocioRequestDto requestDto) {
		return mapper.toResponseDto(negocioUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<NegocioResponseDto> listarTodo() {
		return negocioUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer idNegocio) {
		negocioUseCase.eliminar(idNegocio);
		return ResponseEntity.noContent().build();
	}
}