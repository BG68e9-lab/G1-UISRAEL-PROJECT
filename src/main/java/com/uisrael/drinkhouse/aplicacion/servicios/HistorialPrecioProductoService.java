package com.uisrael.drinkhouse.aplicacion.servicios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialPrecioProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialPrecioProductoJpaRepository;
import com.uisrael.drinkhouse.presentacion.dto.HistorialPrecioProductoDTO;
import com.uisrael.drinkhouse.presentacion.mapeadores.HistorialPrecioProductoMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistorialPrecioProductoService {

	private final HistorialPrecioProductoJpaRepository repository;
	private final HistorialPrecioProductoMapper mapper;

	/**
	 * Obtiene todo el historial de precios de un producto
	 */
	@Transactional(readOnly = true)
	public List<HistorialPrecioProductoDTO> obtenerHistorialPorProducto(Long productoId) {
		log.info("Obteniendo historial de precios para producto ID: {}", productoId);
		
		List<HistorialPrecioProductoEntity> historial = repository.findByProductoIdOrderByFechaCambioDesc(productoId);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	/**
	 * Obtiene historial de precios de un producto con paginación
	 */
	@Transactional(readOnly = true)
	public Page<HistorialPrecioProductoDTO> obtenerHistorialPaginado(Long productoId, int page, int size) {
		log.info("Obteniendo historial de precios paginado para producto ID: {} (page: {}, size: {})", 
			productoId, page, size);
		
		Pageable pageable = PageRequest.of(page, size);
		Page<HistorialPrecioProductoEntity> historialPage = repository.findByProductoIdOrderByFechaCambioDesc(productoId, pageable);
		
		return historialPage.map(mapper::toDTO);
	}

	/**
	 * Obtiene el último cambio de precio de un producto
	 */
	@Transactional(readOnly = true)
	public HistorialPrecioProductoDTO obtenerUltimoCambio(Long productoId) {
		log.info("Obteniendo último cambio de precio para producto ID: {}", productoId);
		
		HistorialPrecioProductoEntity ultimo = repository.findTopByProductoIdOrderByFechaCambioDesc(productoId);
		
		return mapper.toDTO(ultimo);
	}

	/**
	 * Obtiene el precio vigente de un producto en una fecha específica
	 */
	@Transactional(readOnly = true)
	public HistorialPrecioProductoDTO obtenerPrecioEnFecha(Long productoId, OffsetDateTime fecha) {
		log.info("Obteniendo precio vigente para producto ID: {} en fecha: {}", productoId, fecha);
		
		Pageable pageable = PageRequest.of(0, 1);
		List<HistorialPrecioProductoEntity> resultado = repository.findPrecioEnFecha(productoId, fecha, pageable);
		
		if (resultado.isEmpty()) {
			log.warn("No se encontró historial de precio para producto ID: {} en fecha: {}", productoId, fecha);
			return null;
		}
		
		return mapper.toDTO(resultado.get(0));
	}

	/**
	 * Obtiene historial de precios en un rango de fechas
	 */
	@Transactional(readOnly = true)
	public List<HistorialPrecioProductoDTO> obtenerHistorialEnRango(
		Long productoId, 
		OffsetDateTime fechaInicio, 
		OffsetDateTime fechaFin
	) {
		log.info("Obteniendo historial de precios para producto ID: {} entre {} y {}", 
			productoId, fechaInicio, fechaFin);
		
		List<HistorialPrecioProductoEntity> historial = repository.findByProductoIdAndFechaCambioBetween(
			productoId, fechaInicio, fechaFin
		);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	/**
	 * Obtiene historial de precios por factura relacionada
	 */
	@Transactional(readOnly = true)
	public List<HistorialPrecioProductoDTO> obtenerHistorialPorFactura(String numeroFactura) {
		log.info("Obteniendo historial de precios para factura: {}", numeroFactura);
		
		List<HistorialPrecioProductoEntity> historial = repository.findByFacturaRelacionada(numeroFactura);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	/**
	 * Cuenta cambios de precio de un producto
	 */
	@Transactional(readOnly = true)
	public long contarCambiosPorProducto(Long productoId) {
		return repository.countByProductoId(productoId);
	}

	/**
	 * Obtiene historial por origen de cambio
	 */
	@Transactional(readOnly = true)
	public List<HistorialPrecioProductoDTO> obtenerHistorialPorOrigen(String origenCambio) {
		log.info("Obteniendo historial de precios por origen: {}", origenCambio);
		
		List<HistorialPrecioProductoEntity> historial = repository.findByOrigenCambio(origenCambio);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}
}
