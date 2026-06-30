package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Rol;
import com.uisrael.drinkhouse.dominio.repositorios.IRolRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.RolEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IRolJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IRolJpaRepositorio;

public class RolRepositorioImpl implements IRolRepositorio{
	
	private final IRolJpaRepositorio jpaRepositorio;
	private final IRolJpaMapper rolMapper;

	public RolRepositorioImpl(IRolJpaRepositorio jpaRepositorio, IRolJpaMapper rolMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.rolMapper = rolMapper;
	}

	@Override
	public Rol guardar(Rol nuevoRol) {
		RolEntity entity = rolMapper.toEntity(nuevoRol);
		RolEntity guardar = jpaRepositorio.save(entity);
		return rolMapper.toDomain(guardar);
	}

	@Override
	public Optional<Rol> buscarPorId(Integer idRol) {
		return jpaRepositorio.findById(idRol).map(rolMapper::toDomain);
	}

	@Override
	public List<Rol> listarTodos() {
		return jpaRepositorio.findAll().stream().map(rolMapper::toDomain).toList();
	}

	@Override
	public void eliminar(Integer idRol) {
		jpaRepositorio.deleteById(idRol);
	} 

}
