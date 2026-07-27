package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;

public interface IUsuarioUseCase {

	Usuario crearUsuario(Usuario usuario);

	Usuario activarUsuario(UUID id);

	Usuario desactivarUsuario(UUID id);

	Usuario buscarPorId(UUID id);

	List<Usuario> listarConFiltro(String estadoCuenta);

	Usuario actualizarUsuario(UUID id, Usuario usuario);
}
