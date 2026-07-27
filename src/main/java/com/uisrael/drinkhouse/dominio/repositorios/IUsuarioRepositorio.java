package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;

public interface IUsuarioRepositorio {

	Usuario guardar(Usuario usuario);

	Optional<Usuario> buscarPorId(UUID id);

	List<Usuario> listarConFiltro(String estadoCuenta);

	boolean existePorEmail(String email);

	void asignarRol(UUID usuarioId, Integer rolId);

	void revocarRol(UUID usuarioId, Integer rolId);

}
