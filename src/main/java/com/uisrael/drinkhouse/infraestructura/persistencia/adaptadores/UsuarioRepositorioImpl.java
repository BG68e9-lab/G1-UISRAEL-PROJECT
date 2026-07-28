package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.RolEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IRolJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IUsuarioJpaRepositorio;

public class UsuarioRepositorioImpl implements IUsuarioRepositorio {

	private final IUsuarioJpaRepositorio jpaRepositorio;
	private final IUsuarioJpaMapper usuarioMapper;
	private final IRolJpaRepositorio rolJpaRepositorio;

	public UsuarioRepositorioImpl(IUsuarioJpaRepositorio jpaRepositorio,
			IUsuarioJpaMapper usuarioMapper,
			IRolJpaRepositorio rolJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.usuarioMapper = usuarioMapper;
		this.rolJpaRepositorio = rolJpaRepositorio;
	}

	@Override
	public Usuario guardar(Usuario usuario) {
		UsuarioEntity entity = usuarioMapper.toEntity(usuario);
		// Asignar rol si está presente
		if (usuario.getRolId() != null) {
			RolEntity rolRef = new RolEntity();
			rolRef.setRolId(usuario.getRolId());
			entity.setFkRolEntity(rolRef);
		}
		return usuarioMapper.toDomain(jpaRepositorio.save(entity));
	}

	@Override
	public Optional<Usuario> buscarPorId(UUID id) {
		return jpaRepositorio.findById(id).map(usuarioMapper::toDomain);
	}

	@Override
	public Optional<Usuario> buscarPorEmail(String email) {
		return jpaRepositorio.findByEmail(email).map(usuarioMapper::toDomain);
	}

	@Override
	public List<Usuario> listarConFiltro(String estadoCuenta) {
		if (estadoCuenta == null || estadoCuenta.isBlank()) {
			return jpaRepositorio.findAll().stream().map(usuarioMapper::toDomain).toList();
		}
		return jpaRepositorio.findByEstadoCuenta(estadoCuenta)
				.stream().map(usuarioMapper::toDomain).toList();
	}

	@Override
	public boolean existePorEmail(String email) {
		return jpaRepositorio.existsByEmail(email);
	}

	@Override
	public void asignarRol(UUID usuarioId, Integer rolId) {
		UsuarioEntity usuario = jpaRepositorio.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		RolEntity rol = rolJpaRepositorio.findById(rolId)
				.orElseThrow(() -> new RuntimeException("Rol no encontrado"));
		usuario.setFkRolEntity(rol);
		jpaRepositorio.save(usuario);
	}

	@Override
	public void revocarRol(UUID usuarioId, Integer rolId) {
		UsuarioEntity usuario = jpaRepositorio.findById(usuarioId)
				.orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
		if (usuario.getFkRolEntity() != null
				&& usuario.getFkRolEntity().getRolId().equals(rolId)) {
			usuario.setFkRolEntity(null);
			jpaRepositorio.save(usuario);
		}
	}
}
