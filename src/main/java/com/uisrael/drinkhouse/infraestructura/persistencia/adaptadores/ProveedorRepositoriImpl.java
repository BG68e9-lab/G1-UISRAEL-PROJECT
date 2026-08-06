package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.dominio.repositorios.IProveedorRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProveedorJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProveedorJpaRepositorio;

public class ProveedorRepositoriImpl implements IProveedorRepositorio {

	private final IProveedorJpaRepositorio jpaRepositorio;
	private final IProveedorJpaMapper proveedorMapper;

	public ProveedorRepositoriImpl(IProveedorJpaRepositorio jpaRepositorio, IProveedorJpaMapper proveedorMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.proveedorMapper = proveedorMapper;
	}

	@Override
	public Proveedor guardar(Proveedor proveedor) {
		ProveedorEntity entity = proveedorMapper.toEntity(proveedor);
		ProveedorEntity guardado = jpaRepositorio.save(entity);
		return proveedorMapper.toDomain(guardado);
	}

	@Override
	public Optional<Proveedor> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(proveedorMapper::toDomain);
	}

	@Override
	public List<Proveedor> listarTodos() {
		return jpaRepositorio.findAll().stream().map(proveedorMapper::toDomain).toList();
	}

	@Override
	public boolean existePorRuc(String ruc) {
		return jpaRepositorio.existsByRuc(ruc);
	}
	
	@Override
	public Optional<Proveedor> buscarPorRuc(String ruc) {
		return jpaRepositorio.findByRuc(ruc).map(proveedorMapper::toDomain);
	}

	@Override
	public void eliminar(Long id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public boolean tieneOrdenesAsociadas(Long id) {
		return jpaRepositorio.tieneOrdenes(id);
	}
}
