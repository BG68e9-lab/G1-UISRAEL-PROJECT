package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IUsuarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IUsuarioJpaRepositorio;

public class UsuarioRepositorioImpl implements IUsuarioRepositorio {

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
		return jpaRepositorio.findById(idUsuario).map(this::toDomainEnriquecido);
	}

	@Override
	public List<Usuario> listarTodos() {
		return jpaRepositorio.findAll().stream().map(this::toDomainEnriquecido).toList();
	}

	@Override
	public void eliminar(UUID idUsuario) {
		jpaRepositorio.deleteById(idUsuario);
	}

	@Override
	public Optional<Usuario> buscarPorEmail(String email) {
		return jpaRepositorio.findByEmailIgnoreCase(email).map(this::toDomainEnriquecido);
	}

	/**
	 * Igual que usuarioMapper.toDomain(), pero ademas completa negocioId/rolId
	 * a partir de las relaciones de la entidad (el mapper no las conoce porque
	 * el dominio Usuario es "plano"). Solo se usa en lecturas; guardar() sigue
	 * usando el mapper tal cual para no arriesgar las relaciones al persistir.
	 */
	private Usuario toDomainEnriquecido(UsuarioEntity entity) {
		Usuario usuario = usuarioMapper.toDomain(entity);
		if (entity.getFkNegocioEntity() != null) {
			usuario.setNegocioId(entity.getFkNegocioEntity().getNegocioId());
		}
		if (entity.getFkRolEntity() != null) {
			usuario.setRolId(entity.getFkRolEntity().getRolId());
			usuario.setRolNombre(entity.getFkRolEntity().getNombre());
		}
		return usuario;
	}

	@Override
	public void actualizarPasswordHash(UUID idUsuario, String nuevoPasswordHash) {
		// Se actualiza directamente sobre la entidad ya gestionada por JPA para no
		// perder relaciones (negocio, rol, etc.) que el objeto de dominio no expone.
		UsuarioEntity entity = jpaRepositorio.findById(idUsuario)
				.orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
		entity.setPasswordHash(nuevoPasswordHash);
		jpaRepositorio.save(entity);
	}

}
