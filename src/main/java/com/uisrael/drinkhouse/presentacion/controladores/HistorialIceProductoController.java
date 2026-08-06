package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.servicios.HistorialIceProductoService;
import com.uisrael.drinkhouse.presentacion.dto.HistorialIceProductoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/productos/{productoId}/historial-ice")
@RequiredArgsConstructor
@Tag(name = "Historial de ICE", description = "Endpoints para consultar historial de cambios de ICE (Impuesto a Consumos Especiales) de productos")
public class HistorialIceProductoController {

	private final HistorialIceProductoService service;

	@GetMapping
	@Operation(
		summary = "Obtener historial de ICE de un producto",
		description = "Retorna todos los cambios de ICE ordenados por fecha descendente"
	)
	public ResponseEntity<List<HistorialIceProductoDTO>> obtenerHistorial(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-ice", productoId);
		
		List<HistorialIceProductoDTO> historial = service.obtenerHistorialPorProducto(productoId);
		
		return ResponseEntity.ok(historial);
	}

	@GetMapping("/paginado")
	@Operation(
		summary = "Obtener historial de ICE paginado",
		description = "Retorna el historial de ICE con paginación"
	)
	public ResponseEntity<Page<HistorialIceProductoDTO>> obtenerHistorialPaginado(
		@Parameter(description = "ID del producto") @PathVariable Long productoId,
		@Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
		@Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size
	) {
		log.info("GET /api/productos/{}/historial-ice/paginado?page={}&size={}", productoId, page, size);
		
		Page<HistorialIceProductoDTO> historial = service.obtenerHistorialPaginado(productoId, page, size);
		
		return ResponseEntity.ok(historial);
	}

	@GetMapping("/ultimo")
	@Operation(
		summary = "Obtener último cambio de ICE",
		description = "Retorna el cambio de ICE más reciente del producto"
	)
	public ResponseEntity<HistorialIceProductoDTO> obtenerUltimoCambio(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-ice/ultimo", productoId);
		
		HistorialIceProductoDTO ultimo = service.obtenerUltimoCambio(productoId);
		
		if (ultimo == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(ultimo);
	}

	@GetMapping("/en-fecha")
	@Operation(
		summary = "Obtener ICE vigente en una fecha específica",
		description = "Retorna el ICE que estaba vigente en la fecha indicada"
	)
	public ResponseEntity<HistorialIceProductoDTO> obtenerIceEnFecha(
		@Parameter(description = "ID del producto") @PathVariable Long productoId,
		@Parameter(description = "Fecha a consultar (formato: yyyy-MM-dd'T'HH:mm:ss.SSSXXX)") 
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha
	) {
		log.info("GET /api/productos/{}/historial-ice/en-fecha?fecha={}", productoId, fecha);
		
		HistorialIceProductoDTO ice = service.obtenerIceEnFecha(productoId, fecha);
		
		if (ice == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(ice);
	}

	@GetMapping("/count")
	@Operation(
		summary = "Contar cambios de ICE",
		description = "Retorna el número total de cambios de ICE registrados para el producto"
	)
	public ResponseEntity<Long> contarCambios(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-ice/count", productoId);
		
		long count = service.contarCambiosPorProducto(productoId);
		
		return ResponseEntity.ok(count);
	}
}
