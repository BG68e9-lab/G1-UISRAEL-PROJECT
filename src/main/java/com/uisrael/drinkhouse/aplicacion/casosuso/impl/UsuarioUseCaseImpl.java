package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IUsuarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;

public class UsuarioUseCaseImpl implements IUsuarioUseCase {

	private final IUsuarioRepositorio repositorio;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;

	public UsuarioUseCaseImpl(IUsuarioRepositorio repositorio, ILogAuditoriaUseCase logAuditoriaUseCase) {
		this.repositorio = repositorio;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
	}

	@Override
	public Usuario crearUsuario(Usuario usuario) {
		if (usuario.getEmail() != null && !usuario.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
			throw new ReglaNegocioException("Formato de email inválido: " + usuario.getEmail());
		}
		if (repositorio.existePorEmail(usuario.getEmail())) {
			throw new ConflictoUnicoException("Ya existe un usuario con email: " + usuario.getEmail());
		}
		usuario.setEstadoCuenta("PENDIENTE");
		Usuario guardado = repositorio.guardar(usuario);
		logAuditoriaUseCase.registrar("Usuario", guardado.getUsuarioId().toString(), "CREAR", guardado);
		return guardado;
	}

	@Override
	public Usuario activarUsuario(UUID id) {
		Usuario usuario = repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
		if (!"PENDIENTE".equals(usuario.getEstadoCuenta())) {
			throw new ReglaNegocioException("El usuario no está en estado PENDIENTE");
		}
		usuario.setEstadoCuenta("ACTIVO");
		usuario.setActivadoEn(OffsetDateTime.now());
		Usuario actualizado = repositorio.guardar(usuario);
		logAuditoriaUseCase.registrar("Usuario", id.toString(), "ACTIVAR", actualizado);
		return actualizado;
	}

	@Override
	public Usuario desactivarUsuario(UUID id) {
		Usuario usuario = repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
		if (!"ACTIVO".equals(usuario.getEstadoCuenta())) {
			throw new ReglaNegocioException("El usuario no está en estado ACTIVO");
		}
		usuario.setEstadoCuenta("INACTIVO");
		Usuario actualizado = repositorio.guardar(usuario);
		logAuditoriaUseCase.registrar("Usuario", id.toString(), "DESACTIVAR", actualizado);
		return actualizado;
	}

	@Override
	public Usuario buscarPorId(UUID id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
	}

	@Override
	public Usuario buscarPorEmail(String email) {
		return repositorio.buscarPorEmail(email)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
	}

	@Override
	public List<Usuario> listarConFiltro(String estadoCuenta) {
		return repositorio.listarConFiltro(estadoCuenta);
	}

	@Override
	public Usuario actualizarUsuario(UUID id, Usuario usuario) {
		Usuario existente = repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
		existente.setNombreCompleto(usuario.getNombreCompleto());
		existente.setEmail(usuario.getEmail());
		existente.setRolId(usuario.getRolId());
		
		if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().isBlank()) {
			existente.setPasswordHash(usuario.getPasswordHash());
		}
		
		return repositorio.guardar(existente);
	}
}
