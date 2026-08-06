package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.dominio.entidades.Venta;
import com.uisrael.drinkhouse.dominio.repositorios.IVentaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.IVentaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.VentaEntity;

/**
 * Adaptador que implementa el puerto de salida IVentaRepositorio usando JPA.
 */
@Component
public class VentaRepositorioImpl implements IVentaRepositorio {

	private final IVentaJpaRepositorio jpaRepositorio;

	public VentaRepositorioImpl(IVentaJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public Optional<Venta> buscarPorId(Long ventaId) {
		return jpaRepositorio.findById(ventaId)
				.map(this::toDomain);
	}

	private Venta toDomain(VentaEntity entity) {
		Venta venta = new Venta();
		venta.setVentaId(entity.getVentaId());
		venta.setNegocioId(entity.getFkNegocioEntity() != null 
				? entity.getFkNegocioEntity().getNegocioId() 
				: null);
		venta.setCodigoVenta(entity.getCodigoVenta());
		venta.setNombreCliente(entity.getNombreCliente());
		venta.setCedulaRuc(entity.getCedulaRuc());
		venta.setTelefono(entity.getTelefono());
		venta.setEmail(entity.getEmail());
		venta.setMetodoPago(entity.getMetodoPago());
		venta.setSubtotal(entity.getSubtotal());
		venta.setDescuento(entity.getDescuento());
		venta.setIva(entity.getIva());
		venta.setTotal(entity.getTotal());
		venta.setEstado(entity.getEstado());
		venta.setObservaciones(entity.getObservaciones());
		venta.setCreadoEn(entity.getCreadoEn());
		venta.setAnuladoEn(entity.getAnuladoEn());
		return venta;
	}
}
