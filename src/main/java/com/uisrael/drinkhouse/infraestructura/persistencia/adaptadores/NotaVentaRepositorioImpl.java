package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;
import com.uisrael.drinkhouse.dominio.repositorios.INotaVentaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.INotaVentaJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NotaVentaEntity;

@Component
public class NotaVentaRepositorioImpl implements INotaVentaRepositorio {

	private final INotaVentaJpaRepositorio jpaRepositorio;

	public NotaVentaRepositorioImpl(INotaVentaJpaRepositorio jpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
	}

	@Override
	public NotaVenta guardar(NotaVenta notaVenta) {
		NotaVentaEntity entity = toEntity(notaVenta);
		NotaVentaEntity guardada = jpaRepositorio.save(entity);
		return toDomain(guardada);
	}

	@Override
	public List<NotaVenta> listarTodas() {
		return jpaRepositorio.findAllByOrderByCreadoEnDesc()
				.stream()
				.map(this::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<NotaVenta> buscarPorId(Long notaId) {
		return jpaRepositorio.findById(notaId)
				.map(this::toDomain);
	}

	@Override
	public void eliminar(Long notaId) {
		jpaRepositorio.deleteById(notaId);
	}

	private NotaVentaEntity toEntity(NotaVenta dominio) {
		NotaVentaEntity entity = new NotaVentaEntity();
		entity.setNotaId(dominio.getNotaId());
		entity.setFecha(dominio.getFecha());
		entity.setNombreCliente(dominio.getNombreCliente());
		entity.setProductoVendido(dominio.getProductoVendido());
		entity.setPrecioUnitario(dominio.getPrecioUnitario());
		entity.setTotal(dominio.getTotal());
		entity.setObservaciones(dominio.getObservaciones());
		entity.setCreadoEn(dominio.getCreadoEn());
		return entity;
	}

	private NotaVenta toDomain(NotaVentaEntity entity) {
		return new NotaVenta(
				entity.getNotaId(),
				entity.getFecha(),
				entity.getNombreCliente(),
				entity.getProductoVendido(),
				entity.getPrecioUnitario(),
				entity.getTotal(),
				entity.getObservaciones(),
				entity.getCreadoEn()
		);
	}
}
