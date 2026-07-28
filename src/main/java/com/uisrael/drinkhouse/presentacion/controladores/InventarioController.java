package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

/**
 * Endpoints de consulta agregada de inventario que no pertenecen
 * estrictamente a un solo modulo (p. ej. stock disponible usado por el
 * formulario de movimientos del front2 para validar salidas).
 */
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

	private final ILoteProductoUseCase loteProductoUseCase;

	public InventarioController(ILoteProductoUseCase loteProductoUseCase) {
		this.loteProductoUseCase = loteProductoUseCase;
	}

	@GetMapping("/stock-disponible")
	public Map<String, Object> stockDisponible(@RequestParam Long productoId,
			@RequestParam(required = false) Long loteId) {
		int cantidadDisponible;

		if (loteId != null) {
			LoteProducto lote = loteProductoUseCase.buscarPorId(loteId);
			cantidadDisponible = lote.getCantidadDisponible() != null ? lote.getCantidadDisponible() : 0;
		} else {
			cantidadDisponible = loteProductoUseCase.listar(productoId).stream()
					.filter(l -> Boolean.TRUE.equals(l.getActivo()))
					.mapToInt(l -> l.getCantidadDisponible() != null ? l.getCantidadDisponible() : 0)
					.sum();
		}

		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("productoId", productoId);
		respuesta.put("loteId", loteId);
		respuesta.put("cantidadDisponible", cantidadDisponible);
		return respuesta;
	}

	@ExceptionHandler(java.util.NoSuchElementException.class)
	public ResponseEntity<String> manejarNoEncontrado(java.util.NoSuchElementException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}
}
