package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.presentacion.dto.request.SecuenciaCodigoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.SecuenciaCodigoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ISecuenciaCodigoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/secuencias-codigo")
public class SecuenciaCodigoController {

	private final ISecuenciaCodigoUseCase secuenciaCodigoUseCase;
	private final ISecuenciaCodigoDtoMapper mapper;

	public SecuenciaCodigoController(
			ISecuenciaCodigoUseCase secuenciaCodigoUseCase,
			ISecuenciaCodigoDtoMapper mapper) {
		this.secuenciaCodigoUseCase = secuenciaCodigoUseCase;
		this.mapper = mapper;
	}

@GetMapping
	public ResponseEntity<List<SecuenciaCodigoResponseDto>> listarTodas() {
		List<SecuenciaCodigo> secuencias = secuenciaCodigoUseCase.listarTodas();
		List<SecuenciaCodigoResponseDto> response = secuencias.stream()
				.map(mapper::toResponseDto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

@GetMapping("/negocio/{negocioId}")
	public ResponseEntity<List<SecuenciaCodigoResponseDto>> listarPorNegocio(
			@PathVariable Integer negocioId) {
		List<SecuenciaCodigo> secuencias = secuenciaCodigoUseCase.listarPorNegocio(negocioId);
		List<SecuenciaCodigoResponseDto> response = secuencias.stream()
				.map(mapper::toResponseDto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

@GetMapping("/{negocioId}/{tipoMovimientoId}")
	public ResponseEntity<SecuenciaCodigoResponseDto> buscar(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId) {
		SecuenciaCodigo secuencia = secuenciaCodigoUseCase.buscar(negocioId, tipoMovimientoId);
		return ResponseEntity.ok(mapper.toResponseDto(secuencia));
	}

@GetMapping("/{negocioId}/{tipoMovimientoId}/siguiente")
	public ResponseEntity<Long> siguiente(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId) {
		Long numero = secuenciaCodigoUseCase.siguiente(negocioId, tipoMovimientoId);
		return ResponseEntity.ok(numero);
	}

@PostMapping("/{negocioId}/{tipoMovimientoId}")
	public ResponseEntity<SecuenciaCodigoResponseDto> crear(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId,
			@Valid @RequestBody SecuenciaCodigoRequestDto request) {
		
		SecuenciaCodigo secuencia = new SecuenciaCodigo(
				negocioId, 
				tipoMovimientoId, 
				request.getUltimoNumero());
		
		SecuenciaCodigo creada = secuenciaCodigoUseCase.crear(secuencia);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(mapper.toResponseDto(creada));
	}

@PutMapping("/{negocioId}/{tipoMovimientoId}")
	public ResponseEntity<SecuenciaCodigoResponseDto> actualizar(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId,
			@Valid @RequestBody SecuenciaCodigoRequestDto request) {
		
		SecuenciaCodigo actualizada = secuenciaCodigoUseCase.actualizar(
				negocioId, 
				tipoMovimientoId, 
				request.getUltimoNumero());
		
		return ResponseEntity.ok(mapper.toResponseDto(actualizada));
	}

@PutMapping("/{negocioId}/{tipoMovimientoId}/reiniciar")
	public ResponseEntity<SecuenciaCodigoResponseDto> reiniciar(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId,
			@RequestBody(required = false) SecuenciaCodigoRequestDto request) {
		
		Long valorInicial = request != null ? request.getUltimoNumero() : 0L;
		SecuenciaCodigo reiniciada = secuenciaCodigoUseCase.reiniciar(
				negocioId, 
				tipoMovimientoId, 
				valorInicial);
		
		return ResponseEntity.ok(mapper.toResponseDto(reiniciada));
	}

@DeleteMapping("/{negocioId}/{tipoMovimientoId}")
	public ResponseEntity<Void> eliminar(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId) {
		
		secuenciaCodigoUseCase.eliminar(negocioId, tipoMovimientoId);
		return ResponseEntity.noContent().build();
	}

@PostMapping("/inicializar")
	public ResponseEntity<Integer> inicializar() {
		int creadas = secuenciaCodigoUseCase.inicializarSecuenciasParaTodosLosNegocios();
		return ResponseEntity.ok(creadas);
	}
}
