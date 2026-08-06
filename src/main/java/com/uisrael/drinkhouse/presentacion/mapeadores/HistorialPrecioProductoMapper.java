package com.uisrael.drinkhouse.presentacion.mapeadores;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.HistorialPrecioProductoEntity;
import com.uisrael.drinkhouse.presentacion.dto.HistorialPrecioProductoDTO;

@Component
public class HistorialPrecioProductoMapper {

	public HistorialPrecioProductoDTO toDTO(HistorialPrecioProductoEntity entity) {
		if (entity == null) {
			return null;
		}
		
		HistorialPrecioProductoDTO dto = HistorialPrecioProductoDTO.builder()
			.historialPrecioId(entity.getHistorialPrecioId())
			.productoId(entity.getProductoId())
			.costoPromedioAnterior(entity.getCostoPromedioAnterior())
			.margenGananciaAnterior(entity.getMargenGananciaAnterior())
			.precioVentaAnterior(entity.getPrecioVentaAnterior())
			.costoPromedioNuevo(entity.getCostoPromedioNuevo())
			.margenGananciaNuevo(entity.getMargenGananciaNuevo())
			.precioVentaNuevo(entity.getPrecioVentaNuevo())
			.motivo(entity.getMotivo())
			.usuarioModificador(entity.getUsuarioModificador())
			.fechaCambio(entity.getFechaCambio())
			.origenCambio(entity.getOrigenCambio())
			.facturaRelacionada(entity.getFacturaRelacionada())
			.ordenCompraId(entity.getOrdenCompraId())
			.build();
		
		if (entity.getCostoPromedioAnterior() != null && entity.getCostoPromedioNuevo() != null 
			&& entity.getCostoPromedioAnterior().compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal variacionCosto = entity.getCostoPromedioNuevo()
				.subtract(entity.getCostoPromedioAnterior())
				.divide(entity.getCostoPromedioAnterior(), 4, RoundingMode.HALF_UP)
				.multiply(new BigDecimal("100"));
			dto.setVariacionCostoPorcentaje(variacionCosto);
		}
		
		if (entity.getPrecioVentaAnterior() != null && entity.getPrecioVentaNuevo() != null 
			&& entity.getPrecioVentaAnterior().compareTo(BigDecimal.ZERO) != 0) {
			BigDecimal variacionPrecio = entity.getPrecioVentaNuevo()
				.subtract(entity.getPrecioVentaAnterior())
				.divide(entity.getPrecioVentaAnterior(), 4, RoundingMode.HALF_UP)
				.multiply(new BigDecimal("100"));
			dto.setVariacionPrecioVentaPorcentaje(variacionPrecio);
		}
		
		return dto;
	}
	
	public HistorialPrecioProductoEntity toEntity(HistorialPrecioProductoDTO dto) {
		if (dto == null) {
			return null;
		}
		
		HistorialPrecioProductoEntity entity = new HistorialPrecioProductoEntity();
		entity.setHistorialPrecioId(dto.getHistorialPrecioId());
		entity.setProductoId(dto.getProductoId());
		entity.setCostoPromedioAnterior(dto.getCostoPromedioAnterior());
		entity.setMargenGananciaAnterior(dto.getMargenGananciaAnterior());
		entity.setPrecioVentaAnterior(dto.getPrecioVentaAnterior());
		entity.setCostoPromedioNuevo(dto.getCostoPromedioNuevo());
		entity.setMargenGananciaNuevo(dto.getMargenGananciaNuevo());
		entity.setPrecioVentaNuevo(dto.getPrecioVentaNuevo());
		entity.setMotivo(dto.getMotivo());
		entity.setUsuarioModificador(dto.getUsuarioModificador());
		entity.setFechaCambio(dto.getFechaCambio());
		entity.setOrigenCambio(dto.getOrigenCambio());
		entity.setFacturaRelacionada(dto.getFacturaRelacionada());
		entity.setOrdenCompraId(dto.getOrdenCompraId());
		
		return entity;
	}
}
