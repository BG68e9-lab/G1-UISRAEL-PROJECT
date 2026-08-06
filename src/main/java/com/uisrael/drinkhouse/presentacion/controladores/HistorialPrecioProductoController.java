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

import com.uisrael.drinkhouse.aplicacion.servicios.HistorialPrecioProductoService;
import com.uisrael.drinkhouse.presentacion.dto.HistorialPrecioProductoDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/productos/{productoId}/historial-precios")
@RequiredArgsConstructor
@Tag(name = "Historial de Precios", description = "Endpoints para consultar historial de cambios de precios de productos")
public class HistorialPrecioProductoController {

	private final HistorialPrecioProductoService service;

	@GetMapping
	@Operation(
		summary = "Obtener historial de precios de un producto",
		description = "Retorna todos los cambios de precio (costo, margen, precio venta) ordenados por fecha descendente"
	)
	public ResponseEntity<List<HistorialPrecioProductoDTO>> obtenerHistorial(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-precios", productoId);
		
		List<HistorialPrecioProductoDTO> historial = service.obtenerHistorialPorProducto(productoId);
		
		return ResponseEntity.ok(historial);
	}

	@GetMapping("/paginado")
	@Operation(
		summary = "Obtener historial de precios paginado",
		description = "Retorna el historial de precios con paginación"
	)
	public ResponseEntity<Page<HistorialPrecioProductoDTO>> obtenerHistorialPaginado(
		@Parameter(description = "ID del producto") @PathVariable Long productoId,
		@Parameter(description = "Número de página (0-indexed)") @RequestParam(defaultValue = "0") int page,
		@Parameter(description = "Tamaño de página") @RequestParam(defaultValue = "20") int size
	) {
		log.info("GET /api/productos/{}/historial-precios/paginado?page={}&size={}", productoId, page, size);
		
		Page<HistorialPrecioProductoDTO> historial = service.obtenerHistorialPaginado(productoId, page, size);
		
		return ResponseEntity.ok(historial);
	}

	@GetMapping("/ultimo")
	@Operation(
		summary = "Obtener último cambio de precio",
		description = "Retorna el cambio de precio más reciente del producto"
	)
	public ResponseEntity<HistorialPrecioProductoDTO> obtenerUltimoCambio(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-precios/ultimo", productoId);
		
		HistorialPrecioProductoDTO ultimo = service.obtenerUltimoCambio(productoId);
		
		if (ultimo == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(ultimo);
	}

	@GetMapping("/en-fecha")
	@Operation(
		summary = "Obtener precio vigente en una fecha específica",
		description = "Retorna el precio que estaba vigente en la fecha indicada (último cambio antes o igual a esa fecha)"
	)
	public ResponseEntity<HistorialPrecioProductoDTO> obtenerPrecioEnFecha(
		@Parameter(description = "ID del producto") @PathVariable Long productoId,
		@Parameter(description = "Fecha a consultar (formato: yyyy-MM-dd'T'HH:mm:ss.SSSXXX)") 
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha
	) {
		log.info("GET /api/productos/{}/historial-precios/en-fecha?fecha={}", productoId, fecha);
		
		HistorialPrecioProductoDTO precio = service.obtenerPrecioEnFecha(productoId, fecha);
		
		if (precio == null) {
			return ResponseEntity.notFound().build();
		}
		
		return ResponseEntity.ok(precio);
	}

	@GetMapping("/rango")
	@Operation(
		summary = "Obtener historial de precios en un rango de fechas",
		description = "Retorna todos los cambios de precio entre dos fechas específicas"
	)
	public ResponseEntity<List<HistorialPrecioProductoDTO>> obtenerHistorialEnRango(
		@Parameter(description = "ID del producto") @PathVariable Long productoId,
		@Parameter(description = "Fecha inicio") 
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaInicio,
		@Parameter(description = "Fecha fin") 
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaFin
	) {
		log.info("GET /api/productos/{}/historial-precios/rango?fechaInicio={}&fechaFin={}", 
			productoId, fechaInicio, fechaFin);
		
		List<HistorialPrecioProductoDTO> historial = service.obtenerHistorialEnRango(productoId, fechaInicio, fechaFin);
		
		return ResponseEntity.ok(historial);
	}

	@GetMapping("/count")
	@Operation(
		summary = "Contar cambios de precio",
		description = "Retorna el número total de cambios de precio registrados para el producto"
	)
	public ResponseEntity<Long> contarCambios(
		@Parameter(description = "ID del producto") @PathVariable Long productoId
	) {
		log.info("GET /api/productos/{}/historial-precios/count", productoId);
		
		long count = service.contarCambiosPorProducto(productoId);
		
		return ResponseEntity.ok(count);
	}
}
