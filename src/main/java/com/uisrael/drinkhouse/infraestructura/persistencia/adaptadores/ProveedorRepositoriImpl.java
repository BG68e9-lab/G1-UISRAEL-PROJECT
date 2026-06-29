package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.dominio.repositorios.IProveedorRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProveedorJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProveedorJpaRepositorio;

public class ProveedorRepositoriImpl implements IProveedorRepositorio {

	private final IProveedorJpaRepositorio jpaRepositorio;
	private final IProveedorJpaMapper proveedorMapper;

	public ProveedorRepositoriImpl(IProveedorJpaRepositorio jpaRepositorio, IProveedorJpaMapper proveedorMapper) {
		super();
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
	public Optional<Proveedor> buscarPorId(int id) {
		// TODO Auto-generated method stub
		return jpaRepositorio.findById(id).map(proveedorMapper::toDomain);
	}

	@Override
	public List<Proveedor> listarTodos() {
		// TODO Auto-generated method stub
		return jpaRepositorio.findAll().stream().map(proveedorMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		// TODO Auto-generated method stub
		jpaRepositorio.deleteById(id);
	}
}
