package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.UUID;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRolUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Rol;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.dominio.repositorios.IRolRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;

public class RolUseCaseImpl implements IRolUseCase {

	private final IRolRepositorio repositorio;
	private final IUsuarioRepositorio usuarioRepositorio;

	public RolUseCaseImpl(IRolRepositorio repositorio, IUsuarioRepositorio usuarioRepositorio) {
		this.repositorio = repositorio;
		this.usuarioRepositorio = usuarioRepositorio;
	}

	@Override
	public Rol crearRol(Rol rol) {
		if (repositorio.existePorNombre(rol.getNombre())) {
			throw new ConflictoUnicoException("Ya existe un rol con nombre: " + rol.getNombre());
		}
		return repositorio.guardar(rol);
	}

	@Override
	public Rol actualizarRol(Integer id, Rol rol) {
		Rol existente = repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id: " + id));
		existente.setNombre(rol.getNombre());
		existente.setDescripcion(rol.getDescripcion());
		return repositorio.guardar(existente);
	}

	@Override
	public List<Rol> listarRoles() {
		return repositorio.listarTodos();
	}

	@Override
	public void asignarRolAUsuario(UUID usuarioId, Integer rolId) {
		repositorio.buscarPorId(rolId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id: " + rolId));
		usuarioRepositorio.buscarPorId(usuarioId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + usuarioId));
		usuarioRepositorio.asignarRol(usuarioId, rolId);
	}

	@Override
	public void revocarRolDeUsuario(UUID usuarioId, Integer rolId) {
		repositorio.buscarPorId(rolId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id: " + rolId));
		usuarioRepositorio.buscarPorId(usuarioId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + usuarioId));
		usuarioRepositorio.revocarRol(usuarioId, rolId);
	}
}
