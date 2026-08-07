package com.uisrael.drinkhouse.aplicacion.servicios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialIceProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialIceProductoJpaRepository;
import com.uisrael.drinkhouse.presentacion.dto.HistorialIceProductoDTO;
import com.uisrael.drinkhouse.presentacion.mapeadores.HistorialIceProductoMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HistorialIceProductoService {

	private final HistorialIceProductoJpaRepository repository;
	private final HistorialIceProductoMapper mapper;

	public List<HistorialIceProductoDTO> obtenerHistorialPorProducto(Long productoId) {
		log.info("Obteniendo historial de ICE para producto ID: {}", productoId);
		
		List<HistorialIceProductoEntity> historial = repository.findByProductoIdOrderByFechaCambioDesc(productoId);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	public Page<HistorialIceProductoDTO> obtenerHistorialPaginado(Long productoId, int page, int size) {
		log.info("Obteniendo historial de ICE paginado para producto ID: {} (page: {}, size: {})", 
			productoId, page, size);
		
		Pageable pageable = PageRequest.of(page, size);
		Page<HistorialIceProductoEntity> historialPage = repository.findByProductoIdOrderByFechaCambioDesc(productoId, pageable);
		
		return historialPage.map(mapper::toDTO);
	}

	public HistorialIceProductoDTO obtenerUltimoCambio(Long productoId) {
		log.info("Obteniendo último cambio de ICE para producto ID: {}", productoId);
		
		HistorialIceProductoEntity ultimo = repository.findTopByProductoIdOrderByFechaCambioDesc(productoId);
		
		return mapper.toDTO(ultimo);
	}

	public HistorialIceProductoDTO obtenerIceEnFecha(Long productoId, OffsetDateTime fecha) {
		log.info("Obteniendo ICE vigente para producto ID: {} en fecha: {}", productoId, fecha);
		
		Pageable pageable = PageRequest.of(0, 1);
		List<HistorialIceProductoEntity> resultado = repository.findIceEnFecha(productoId, fecha, pageable);
		
		if (resultado.isEmpty()) {
			log.warn("No se encontró historial de ICE para producto ID: {} en fecha: {}", productoId, fecha);
			return null;
		}
		
		return mapper.toDTO(resultado.get(0));
	}

	public List<HistorialIceProductoDTO> obtenerProductosPorGrupo(String grupoIce) {
		log.info("Obteniendo productos con ICE del grupo: {}", grupoIce);
		
		List<HistorialIceProductoEntity> historial = repository.findByGrupoIceOrderByFechaCambioDesc(grupoIce);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	public List<HistorialIceProductoDTO> obtenerProductosActivosConIce(String grupoIce) {
		log.info("Obteniendo productos activos con ICE del grupo: {}", grupoIce);
		
		List<HistorialIceProductoEntity> historial = repository.findProductosActivosConIcePorGrupo(grupoIce);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}

	public long contarCambiosPorProducto(Long productoId) {
		return repository.countByProductoId(productoId);
	}

	public List<HistorialIceProductoDTO> obtenerHistorialPorOrigen(String origenCambio) {
		log.info("Obteniendo historial de ICE por origen: {}", origenCambio);
		
		List<HistorialIceProductoEntity> historial = repository.findByOrigenCambioOrderByFechaCambioDesc(origenCambio);
		
		return historial.stream()
			.map(mapper::toDTO)
			.collect(Collectors.toList());
	}
}
