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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class DetalleOrdenCompraRepositorioImpl implements IDetalleOrdenCompraRepositorio {

	private final IDetalleOrdenCompraJpaRepositorio jpaRepositorio;
	private final IDetalleOrdenCompraJpaMapper mapper;
	
	@PersistenceContext
	private EntityManager entityManager;

	public DetalleOrdenCompraRepositorioImpl(IDetalleOrdenCompraJpaRepositorio jpaRepositorio,
			IDetalleOrdenCompraJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public DetalleOrdenCompra guardar(DetalleOrdenCompra detalle) {
		DetalleOrdenCompraEntity entity = mapper.toEntity(detalle);
		if (detalle.getProductoId() != null) {
			ProductoEntity productoRef = entityManager.getReference(ProductoEntity.class, detalle.getProductoId());
			entity.setFkProductoEntity(productoRef);
		}
		DetalleOrdenCompraEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public DetalleOrdenCompra guardarConOrdenCompraId(DetalleOrdenCompra detalle, Long ordenCompraId) {
		DetalleOrdenCompraEntity entity = mapper.toEntity(detalle);
		OrdenCompraEntity ordenCompraRef = entityManager.getReference(OrdenCompraEntity.class, ordenCompraId);
		entity.setOrdenCompraId(ordenCompraRef);
		if (detalle.getProductoId() != null) {
			ProductoEntity productoRef = entityManager.getReference(ProductoEntity.class, detalle.getProductoId());
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
