package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.CodigoAccesoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.CodigoAccesoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ICodigoAccesoDtoMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/codigos-acceso")
public class CodigoAccesoController {

	private final ICodigoAccesoUseCase codigoAccesoUseCase;
	private final ICodigoAccesoDtoMapper mapper;

	public CodigoAccesoController(ICodigoAccesoUseCase codigoAccesoUseCase, ICodigoAccesoDtoMapper mapper) {
		this.codigoAccesoUseCase = codigoAccesoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CodigoAccesoResponseDto guardar(@Valid @RequestBody CodigoAccesoRequestDto requestDto) {
		return mapper.toResponseDto(codigoAccesoUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<CodigoAccesoResponseDto> listarTodo() {
		return codigoAccesoUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") UUID idCodigoAcceso) {
		codigoAccesoUseCase.eliminar(idCodigoAcceso);
		return ResponseEntity.noContent().build();
	}
}