package com.uisrael.drinkhouse.presentacion.mapeadores;

import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialIceProductoEntity;
import com.uisrael.drinkhouse.presentacion.dto.HistorialIceProductoDTO;

@Component
public class HistorialIceProductoMapper {

	public HistorialIceProductoDTO toDTO(HistorialIceProductoEntity entity) {
		if (entity == null) {
			return null;
		}
		
		return HistorialIceProductoDTO.builder()
			.historialIceId(entity.getHistorialIceId())
			.productoId(entity.getProductoId())
			.aplicaIceAnterior(entity.getAplicaIceAnterior())
			.tarifaIceAnterior(entity.getTarifaIceAnterior())
			.valorEspecificoAnterior(entity.getValorEspecificoAnterior())
			.tipoTarifaAnterior(entity.getTipoTarifaAnterior())
			.aplicaIceNuevo(entity.getAplicaIceNuevo())
			.tarifaIceNueva(entity.getTarifaIceNueva())
			.valorEspecificoNuevo(entity.getValorEspecificoNuevo())
			.tipoTarifaNuevo(entity.getTipoTarifaNuevo())
			.grupoIce(entity.getGrupoIce())
			.esMonofasico(entity.getEsMonofasico())
			.motivo(entity.getMotivo())
			.usuarioModificador(entity.getUsuarioModificador())
			.fechaCambio(entity.getFechaCambio())
			.origenCambio(entity.getOrigenCambio())
			.resolucionSri(entity.getResolucionSri())
			.fechaVigencia(entity.getFechaVigencia())
			.build();
	}
	
	public HistorialIceProductoEntity toEntity(HistorialIceProductoDTO dto) {
		if (dto == null) {
			return null;
		}
		
		HistorialIceProductoEntity entity = new HistorialIceProductoEntity();
		entity.setHistorialIceId(dto.getHistorialIceId());
		entity.setProductoId(dto.getProductoId());
		entity.setAplicaIceAnterior(dto.getAplicaIceAnterior());
		entity.setTarifaIceAnterior(dto.getTarifaIceAnterior());
		entity.setValorEspecificoAnterior(dto.getValorEspecificoAnterior());
		entity.setTipoTarifaAnterior(dto.getTipoTarifaAnterior());
		entity.setAplicaIceNuevo(dto.getAplicaIceNuevo());
		entity.setTarifaIceNueva(dto.getTarifaIceNueva());
		entity.setValorEspecificoNuevo(dto.getValorEspecificoNuevo());
		entity.setTipoTarifaNuevo(dto.getTipoTarifaNuevo());
		entity.setGrupoIce(dto.getGrupoIce());
		entity.setEsMonofasico(dto.getEsMonofasico());
		entity.setMotivo(dto.getMotivo());
		entity.setUsuarioModificador(dto.getUsuarioModificador());
		entity.setFechaCambio(dto.getFechaCambio());
		entity.setOrigenCambio(dto.getOrigenCambio());
		entity.setResolucionSri(dto.getResolucionSri());
		entity.setFechaVigencia(dto.getFechaVigencia());
		
		return entity;
	}
}
