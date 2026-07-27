package com.uisrael.drinkhouse.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INegocioUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.NegocioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.NegocioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.INegocioDtoMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/negocio")
public class NegocioController {

	private final INegocioUseCase negocioUseCase;
	private final INegocioDtoMapper mapper;

	public NegocioController(INegocioUseCase negocioUseCase, INegocioDtoMapper mapper) {
		this.negocioUseCase = negocioUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<NegocioResponseDto> crearNegocio(@Valid @RequestBody NegocioRequestDto requestDto) {
		NegocioResponseDto response = mapper.toResponseDto(
				negocioUseCase.crearNegocio(mapper.toDomain(requestDto)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<NegocioResponseDto> actualizarNegocio(
			@PathVariable Integer id,
			@Valid @RequestBody NegocioRequestDto requestDto) {
		NegocioResponseDto response = mapper.toResponseDto(
				negocioUseCase.actualizarNegocio(id, mapper.toDomain(requestDto)));
		return ResponseEntity.ok(response);
	}

	@GetMapping("/activo")
	public ResponseEntity<NegocioResponseDto> buscarActivo() {
		return ResponseEntity.ok(mapper.toResponseDto(negocioUseCase.buscarActivo()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<NegocioResponseDto> buscarPorId(@PathVariable Integer id) {
		return ResponseEntity.ok(mapper.toResponseDto(negocioUseCase.buscarPorId(id)));
	}
}
