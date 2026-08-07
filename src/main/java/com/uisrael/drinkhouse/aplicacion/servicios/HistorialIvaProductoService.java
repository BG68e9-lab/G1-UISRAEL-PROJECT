package com.uisrael.drinkhouse.aplicacion.servicios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialIvaProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialIvaProductoJpaRepository;
import com.uisrael.drinkhouse.presentacion.dto.HistorialIvaProductoDTO;
import com.uisrael.drinkhouse.presentacion.mapeadores.HistorialIvaProductoMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HistorialIvaProductoService {

	private final HistorialIvaProductoJpaRepository repository;
	private final HistorialIvaProductoMapper mapper;

	public List<HistorialIvaProductoDTO> obtenerHistorialPorProducto(Long productoId) {
		log.info("Obteniendo historial de IVA para producto ID: {}", productoId);
		
		List<HistorialIvaProductoEntity> historial = repository.findByProductoIdOrderByFechaCambioDesc(productoId);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	public Page<HistorialIvaProductoDTO> obtenerHistorialPaginado(Long productoId, int page, int size) {
		log.info("Obteniendo historial de IVA paginado para producto ID: {} (page: {}, size: {})", 
			productoId, page, size);
		
		Pageable pageable = PageRequest.of(page, size);
		Page<HistorialIvaProductoEntity> historialPage = repository.findByProductoIdOrderByFechaCambioDesc(productoId, pageable);
		
		return historialPage.map(mapper::toDTO);
	}

	public HistorialIvaProductoDTO obtenerUltimoCambio(Long productoId) {
		log.info("Obteniendo último cambio de IVA para producto ID: {}", productoId);
		
		HistorialIvaProductoEntity ultimo = repository.findTopByProductoIdOrderByFechaCambioDesc(productoId);
		
		return mapper.toDTO(ultimo);
	}

	public HistorialIvaProductoDTO obtenerIvaEnFecha(Long productoId, OffsetDateTime fecha) {
		log.info("Obteniendo IVA vigente para producto ID: {} en fecha: {}", productoId, fecha);
		
		Pageable pageable = PageRequest.of(0, 1);
		List<HistorialIvaProductoEntity> resultado = repository.findIvaEnFecha(productoId, fecha, pageable);
		
		if (resultado.isEmpty()) {
			log.warn("No se encontró historial de IVA para producto ID: {} en fecha: {}", productoId, fecha);
			return null;
		}
		
		return mapper.toDTO(resultado.get(0));
	}

	public List<HistorialIvaProductoDTO> obtenerProductosPorReformaTributaria(String resolucionSri) {
		log.info("Obteniendo productos afectados por reforma tributaria: {}", resolucionSri);
		
		List<HistorialIvaProductoEntity> historial = repository.findByReformaTributaria(resolucionSri);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	public long contarCambiosPorProducto(Long productoId) {
		return repository.countByProductoId(productoId);
	}

	public List<HistorialIvaProductoDTO> obtenerHistorialPorOrigen(String origenCambio) {
		log.info("Obteniendo historial de IVA por origen: {}", origenCambio);
		
		List<HistorialIvaProductoEntity> historial = repository.findByOrigenCambioOrderByFechaCambioDesc(origenCambio);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}
}
