package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.dominio.repositorios.IOrdenCompraRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoOcEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IOrdenCompraJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoOcJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IOrdenCompraJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProveedorJpaRepositorio;

public class OrdenCompraRepositorioImpl implements IOrdenCompraRepositorio {

	private final IOrdenCompraJpaRepositorio jpaRepositorio;
	private final IOrdenCompraJpaMapper mapper;
	private final IProveedorJpaRepositorio proveedorJpaRepositorio;
	private final IEstadoOcJpaRepositorio estadoOcJpaRepositorio;

	public OrdenCompraRepositorioImpl(IOrdenCompraJpaRepositorio jpaRepositorio,
			IOrdenCompraJpaMapper mapper,
			IProveedorJpaRepositorio proveedorJpaRepositorio,
			IEstadoOcJpaRepositorio estadoOcJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
		this.proveedorJpaRepositorio = proveedorJpaRepositorio;
		this.estadoOcJpaRepositorio = estadoOcJpaRepositorio;
	}

	@Override
	public OrdenCompra guardar(OrdenCompra orden) {
		OrdenCompraEntity entity = mapper.toEntity(orden);
		if (orden.getOrdenCompraId() != null) {
			jpaRepositorio.findById(orden.getOrdenCompraId()).ifPresent(existente -> {
				entity.setFkProveedorEntity(existente.getFkProveedorEntity());
				entity.setFkNegocioEntity(existente.getFkNegocioEntity());
				entity.setExtraidoPorIa(existente.getExtraidoPorIa());
				entity.setFechaOc(existente.getFechaOc());
				entity.setNumeroOc(existente.getNumeroOc());
				if (entity.getConfirmadoPor() == null && existente.getConfirmadoPor() != null) {
					entity.setConfirmadoPor(existente.getConfirmadoPor());
				}
				if (entity.getConfirmadoEn() == null && existente.getConfirmadoEn() != null) {
					entity.setConfirmadoEn(existente.getConfirmadoEn());
				}
			});
		}
		if (orden.getEstado() != null) {
			EstadoOcEntity estadoEntity = estadoOcJpaRepositorio.findByCodigo(orden.getEstado())
					.orElse(null);
			entity.setFkEstadoOcEntity(estadoEntity);
		}
		OrdenCompraEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public OrdenCompra guardarConRelaciones(OrdenCompra orden, Long proveedorId) {
		OrdenCompraEntity entity = mapper.toEntity(orden);
		if (proveedorId != null) {
			ProveedorEntity proveedorEntity = new ProveedorEntity();
			proveedorEntity.setProveedorId(proveedorId);
			entity.setFkProveedorEntity(proveedorEntity);
		}
		if (orden.getEstado() != null) {
			EstadoOcEntity estadoEntity = estadoOcJpaRepositorio.findByCodigo(orden.getEstado())
					.orElse(null);
			entity.setFkEstadoOcEntity(estadoEntity);
		}
		OrdenCompraEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public Optional<OrdenCompra> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<OrdenCompra> buscarConFiltros(String estado, OffsetDateTime desde, OffsetDateTime hasta) {
		return jpaRepositorio.buscarConFiltros(estado, desde, hasta)
				.stream().map(mapper::toDomain).toList();
	}
}
