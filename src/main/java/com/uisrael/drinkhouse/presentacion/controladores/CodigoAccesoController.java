package com.uisrael.drinkhouse.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.CodigoAccesoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.request.ValidarCodigoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.CodigoAccesoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ICodigoAccesoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/codigos-acceso")
public class CodigoAccesoController {

	private final ICodigoAccesoUseCase codigoAccesoUseCase;
	private final ICodigoAccesoDtoMapper mapper;

	public CodigoAccesoController(ICodigoAccesoUseCase codigoAccesoUseCase, ICodigoAccesoDtoMapper mapper) {
		this.codigoAccesoUseCase = codigoAccesoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<CodigoAccesoResponseDto> generar(@Valid @RequestBody CodigoAccesoRequestDto requestDto) {
		CodigoAccesoResponseDto response = mapper.toResponseDto(
				codigoAccesoUseCase.generarCodigo(requestDto.getTipoCodigo(), requestDto.getUsuarioId()));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PostMapping("/validar")
	public ResponseEntity<CodigoAccesoResponseDto> validar(@Valid @RequestBody ValidarCodigoRequestDto requestDto) {
		CodigoAccesoResponseDto response = mapper.toResponseDto(
				codigoAccesoUseCase.validarCodigo(requestDto.getCodigoHash()));
		return ResponseEntity.ok(response);
	}
}
