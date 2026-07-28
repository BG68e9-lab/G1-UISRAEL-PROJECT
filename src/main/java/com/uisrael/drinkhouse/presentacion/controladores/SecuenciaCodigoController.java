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

/**
 * Controlador REST para la gestión de Secuencias de Código.
 * 
 * Las secuencias de código se usan para generar números consecutivos únicos
 * para documentos como órdenes de compra, movimientos de inventario, etc.
 */
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

	/**
	 * Lista todas las secuencias de código configuradas en el sistema.
	 * 
	 * @return lista de secuencias
	 */
	@GetMapping
	public ResponseEntity<List<SecuenciaCodigoResponseDto>> listarTodas() {
		List<SecuenciaCodigo> secuencias = secuenciaCodigoUseCase.listarTodas();
		List<SecuenciaCodigoResponseDto> response = secuencias.stream()
				.map(mapper::toResponseDto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

	/**
	 * Lista las secuencias de código de un negocio específico.
	 * 
	 * @param negocioId ID del negocio
	 * @return lista de secuencias del negocio
	 */
	@GetMapping("/negocio/{negocioId}")
	public ResponseEntity<List<SecuenciaCodigoResponseDto>> listarPorNegocio(
			@PathVariable Integer negocioId) {
		List<SecuenciaCodigo> secuencias = secuenciaCodigoUseCase.listarPorNegocio(negocioId);
		List<SecuenciaCodigoResponseDto> response = secuencias.stream()
				.map(mapper::toResponseDto)
				.collect(Collectors.toList());
		return ResponseEntity.ok(response);
	}

	/**
	 * Busca una secuencia específica por negocio y tipo de movimiento.
	 * 
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @return secuencia encontrada
	 */
	@GetMapping("/{negocioId}/{tipoMovimientoId}")
	public ResponseEntity<SecuenciaCodigoResponseDto> buscar(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId) {
		SecuenciaCodigo secuencia = secuenciaCodigoUseCase.buscar(negocioId, tipoMovimientoId);
		return ResponseEntity.ok(mapper.toResponseDto(secuencia));
	}

	/**
	 * Genera el siguiente número de secuencia para un negocio y tipo de movimiento.
	 * 
	 * Este endpoint incrementa automáticamente el contador y devuelve el nuevo número.
	 * Es thread-safe y usa optimistic locking.
	 * 
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @return siguiente número de secuencia
	 */
	@GetMapping("/{negocioId}/{tipoMovimientoId}/siguiente")
	public ResponseEntity<Long> siguiente(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId) {
		Long numero = secuenciaCodigoUseCase.siguiente(negocioId, tipoMovimientoId);
		return ResponseEntity.ok(numero);
	}

	/**
	 * Crea una nueva secuencia de código.
	 * 
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @param request          datos de la secuencia (último número)
	 * @return secuencia creada
	 */
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

	/**
	 * Actualiza el último número de una secuencia existente.
	 * 
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @param request          nuevo último número
	 * @return secuencia actualizada
	 */
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

	/**
	 * Reinicia una secuencia a un valor específico.
	 * 
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @param request          valor inicial (si es null, reinicia a 0)
	 * @return secuencia reiniciada
	 */
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

	/**
	 * Elimina una secuencia de código.
	 * 
	 * @param negocioId        ID del negocio
	 * @param tipoMovimientoId ID del tipo de movimiento
	 * @return respuesta sin contenido
	 */
	@DeleteMapping("/{negocioId}/{tipoMovimientoId}")
	public ResponseEntity<Void> eliminar(
			@PathVariable Integer negocioId,
			@PathVariable Integer tipoMovimientoId) {
		
		secuenciaCodigoUseCase.eliminar(negocioId, tipoMovimientoId);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Inicializa todas las secuencias necesarias en el sistema.
	 * 
	 * Este endpoint crea automáticamente todas las combinaciones de
	 * negocio × tipo de movimiento que falten en el sistema.
	 * 
	 * @return número de secuencias creadas
	 */
	@PostMapping("/inicializar")
	public ResponseEntity<Integer> inicializar() {
		int creadas = secuenciaCodigoUseCase.inicializarSecuenciasParaTodosLosNegocios();
		return ResponseEntity.ok(creadas);
	}
}
