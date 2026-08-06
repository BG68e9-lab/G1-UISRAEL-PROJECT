package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.INotaVentaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.response.NotaVentaResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.INotaVentaDtoMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Controlador REST para consulta de notas de venta.
 * Solo operaciones de lectura (visualización).
 */
@RestController
@RequestMapping("/api/v1/notas-venta")
@Tag(name = "Notas de Venta", description = "Endpoints para consultar notas de venta simplificadas")
public class NotaVentaController {

	private final INotaVentaUseCase notaVentaUseCase;
	private final INotaVentaDtoMapper mapper;

	public NotaVentaController(INotaVentaUseCase notaVentaUseCase, INotaVentaDtoMapper mapper) {
		this.notaVentaUseCase = notaVentaUseCase;
		this.mapper = mapper;
	}

	/**
	 * Lista todas las notas de venta ordenadas por fecha descendente.
	 * GET /api/v1/notas-venta
	 */
	@GetMapping
	@Operation(summary = "Listar todas las notas de venta", 
			description = "Obtiene todas las notas de venta ordenadas por fecha de creación (más recientes primero)")
	public ResponseEntity<List<NotaVentaResponseDto>> listarTodas() {
		List<NotaVentaResponseDto> lista = notaVentaUseCase.listarTodas()
				.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(lista);
	}

	/**
	 * Busca una nota de venta específica por ID.
	 * GET /api/v1/notas-venta/{id}
	 */
	@GetMapping("/{id}")
	@Operation(summary = "Obtener nota de venta por ID", 
			description = "Obtiene una nota de venta específica usando su ID")
	public ResponseEntity<NotaVentaResponseDto> buscarPorId(@PathVariable Long id) {
		NotaVentaResponseDto dto = mapper.toResponseDto(notaVentaUseCase.buscarPorId(id));
		return ResponseEntity.ok(dto);
	}
}
