package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.repositorios.IDetalleOrdenCompraRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.DetalleOrdenCompraEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IDetalleOrdenCompraJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IDetalleOrdenCompraJpaRepositorio;

public class DetalleOrdenCompraRepositorioImpl implements IDetalleOrdenCompraRepositorio {

	private final IDetalleOrdenCompraJpaRepositorio jpaRepositorio;
	private final IDetalleOrdenCompraJpaMapper mapper;

	public DetalleOrdenCompraRepositorioImpl(IDetalleOrdenCompraJpaRepositorio jpaRepositorio,
			IDetalleOrdenCompraJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public DetalleOrdenCompra guardar(DetalleOrdenCompra detalle) {
		DetalleOrdenCompraEntity entity = mapper.toEntity(detalle);
		// Asignar la relación con el Producto por referencia de ID
		if (detalle.getProductoId() != null) {
			ProductoEntity productoRef = new ProductoEntity();
			productoRef.setProductoId(detalle.getProductoId());
			entity.setFkProductoEntity(productoRef);
		}
		DetalleOrdenCompraEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public DetalleOrdenCompra guardarConOrdenCompraId(DetalleOrdenCompra detalle, Long ordenCompraId) {
		DetalleOrdenCompraEntity entity = mapper.toEntity(detalle);
		// Asignar la relación con la OrdenCompra por referencia de ID
		OrdenCompraEntity ordenCompraRef = new OrdenCompraEntity();
		ordenCompraRef.setOrdenCompraId(ordenCompraId);
		entity.setOrdenCompraId(ordenCompraRef);
		// Asignar la relación con el Producto por referencia de ID
		if (detalle.getProductoId() != null) {
			ProductoEntity productoRef = new ProductoEntity();
			productoRef.setProductoId(detalle.getProductoId());
			entity.setFkProductoEntity(productoRef);
		}
		DetalleOrdenCompraEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public List<DetalleOrdenCompra> buscarPorOrdenCompraId(Long ordenCompraId) {
		return jpaRepositorio.findByOrdenCompraIdOrdenCompraId(ordenCompraId)
				.stream().map(mapper::toDomain).toList();
	}

	@Override
	@Transactional
	public void eliminarPorOrdenCompraId(Long ordenCompraId) {
		jpaRepositorio.deleteByOrdenCompraIdOrdenCompraId(ordenCompraId);
	}
}
