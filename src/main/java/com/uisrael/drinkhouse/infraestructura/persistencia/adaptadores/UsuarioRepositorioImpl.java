package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IUsuarioJpaRepositorio;

public class UsuarioRepositorioImpl implements IUsuarioRepositorio{
	
	private final IUsuarioJpaRepositorio jpaRepositorio;
	private final IUsuarioJpaMapper usuarioMapper;

	public UsuarioRepositorioImpl(IUsuarioJpaRepositorio jpaRepositorio, IUsuarioJpaMapper usuarioMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.usuarioMapper = usuarioMapper;
	}

	@Override
	public Usuario guardar(Usuario nuevoUsuario) {
		UsuarioEntity entity = usuarioMapper.toEntity(nuevoUsuario);
		UsuarioEntity guardar = jpaRepositorio.save(entity);
		return usuarioMapper.toDomain(guardar);
	}

	@Override
	public Optional<Usuario> buscarPorId(UUID idUsuario) {
		return jpaRepositorio.findById(idUsuario).map(usuarioMapper::toDomain);
	}

	@Override
	public List<Usuario> listarTodos() {
		return jpaRepositorio.findAll().stream().map(usuarioMapper::toDomain).toList();
	}

	@Override
	public void eliminar(UUID idUsuario) {
		jpaRepositorio.deleteById(idUsuario);
	}

}
