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

import com.uisrael.drinkhouse.aplicacion.servicios.HistorialIvaProductoService;
import com.uisrael.drinkhouse.presentacion.dto.HistorialIvaProductoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/productos/{productoId}/historial-iva")
@RequiredArgsConstructor
@Tag(name = "Historial de IVA", description = "Endpoints para consultar historial de cambios de tarifa IVA de productos")
public class HistorialIvaProductoController {

	private final HistorialIvaProductoService service;

	@GetMapping
	@Operation(
		summary = "Obtener historial de IVA de un producto",
		description = "Retorna todos los cambios de tarifa IVA ordenados por fecha descendente"
	)
	public ResponseEntity<List<HistorialIvaProductoDTO>> obtenerHistorial(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-iva", productoId);
		
		List<HistorialIvaProductoDTO> historial = service.obtenerHistorialPorProducto(productoId);
		
		return ResponseEntity.ok(historial);
	}

	@GetMapping("/paginado")
	@Operation(
		summary = "Obtener historial de IVA paginado",
		description = "Retorna el historial de IVA con paginación"
	)
	public ResponseEntity<Page<HistorialIvaProductoDTO>> obtenerHistorialPaginado(
		@Parameter(description = "ID del producto") @PathVariable Long productoId,
		@Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
		@Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size
	) {
		log.info("GET /api/productos/{}/historial-iva/paginado?page={}&size={}", productoId, page, size);
		
		Page<HistorialIvaProductoDTO> historial = service.obtenerHistorialPaginado(productoId, page, size);
		
		return ResponseEntity.ok(historial);
	}

	@GetMapping("/ultimo")
	@Operation(
		summary = "Obtener último cambio de IVA",
		description = "Retorna el cambio de IVA más reciente del producto"
	)
	public ResponseEntity<HistorialIvaProductoDTO> obtenerUltimoCambio(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-iva/ultimo", productoId);
		
		HistorialIvaProductoDTO ultimo = service.obtenerUltimoCambio(productoId);
		
		if (ultimo == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(ultimo);
	}

	@GetMapping("/en-fecha")
	@Operation(
		summary = "Obtener IVA vigente en una fecha específica",
		description = "Retorna la tarifa IVA que estaba vigente en la fecha indicada"
	)
	public ResponseEntity<HistorialIvaProductoDTO> obtenerIvaEnFecha(
		@Parameter(description = "ID del producto") @PathVariable Long productoId,
		@Parameter(description = "Fecha a consultar (formato: yyyy-MM-dd'T'HH:mm:ss.SSSXXX)") 
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha
	) {
		log.info("GET /api/productos/{}/historial-iva/en-fecha?fecha={}", productoId, fecha);
		
		HistorialIvaProductoDTO iva = service.obtenerIvaEnFecha(productoId, fecha);
		
		if (iva == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(iva);
	}

	@GetMapping("/count")
	@Operation(
		summary = "Contar cambios de IVA",
		description = "Retorna el número total de cambios de IVA registrados para el producto"
	)
	public ResponseEntity<Long> contarCambios(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-iva/count", productoId);
		
		long count = service.contarCambiosPorProducto(productoId);
		
		return ResponseEntity.ok(count);
	}
}
