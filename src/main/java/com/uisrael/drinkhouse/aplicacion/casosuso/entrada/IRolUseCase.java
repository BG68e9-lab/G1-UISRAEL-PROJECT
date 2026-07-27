package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.Rol;

public interface IRolUseCase {

	Rol crearRol(Rol rol);

	Rol actualizarRol(Integer id, Rol rol);

	List<Rol> listarRoles();

	void asignarRolAUsuario(UUID usuarioId, Integer rolId);

	void revocarRolDeUsuario(UUID usuarioId, Integer rolId);
}
