package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialIvaProductoEntity;
import com.uisrael.drinkhouse.presentacion.dto.HistorialIvaProductoDTO;

@Component
public class HistorialIvaProductoMapper {

	public HistorialIvaProductoDTO toDTO(HistorialIvaProductoEntity entity) {
		if (entity == null) {
			return null;
		}
		
		return HistorialIvaProductoDTO.builder()
			.historialIvaId(entity.getHistorialIvaId())
			.productoId(entity.getProductoId())
			.tarifaIvaAnterior(entity.getTarifaIvaAnterior())
			.codigoPorcentajeAnterior(entity.getCodigoPorcentajeAnterior())
			.descripcionAnterior(entity.getDescripcionAnterior())
			.tarifaIvaNueva(entity.getTarifaIvaNueva())
			.codigoPorcentajeNuevo(entity.getCodigoPorcentajeNuevo())
			.descripcionNueva(entity.getDescripcionNueva())
			.motivo(entity.getMotivo())
			.usuarioModificador(entity.getUsuarioModificador())
			.fechaCambio(entity.getFechaCambio())
			.origenCambio(entity.getOrigenCambio())
			.resolucionSri(entity.getResolucionSri())
			.fechaVigencia(entity.getFechaVigencia())
			.build();
	}
	
	public HistorialIvaProductoEntity toEntity(HistorialIvaProductoDTO dto) {
		if (dto == null) {
			return null;
		}
		
		HistorialIvaProductoEntity entity = new HistorialIvaProductoEntity();
		entity.setHistorialIvaId(dto.getHistorialIvaId());
		entity.setProductoId(dto.getProductoId());
		entity.setTarifaIvaAnterior(dto.getTarifaIvaAnterior());
		entity.setCodigoPorcentajeAnterior(dto.getCodigoPorcentajeAnterior());
		entity.setDescripcionAnterior(dto.getDescripcionAnterior());
		entity.setTarifaIvaNueva(dto.getTarifaIvaNueva());
		entity.setCodigoPorcentajeNuevo(dto.getCodigoPorcentajeNuevo());
		entity.setDescripcionNueva(dto.getDescripcionNueva());
		entity.setMotivo(dto.getMotivo());
		entity.setUsuarioModificador(dto.getUsuarioModificador());
		entity.setFechaCambio(dto.getFechaCambio());
		entity.setOrigenCambio(dto.getOrigenCambio());
		entity.setResolucionSri(dto.getResolucionSri());
		entity.setFechaVigencia(dto.getFechaVigencia());
		
		return entity;
	}
}
