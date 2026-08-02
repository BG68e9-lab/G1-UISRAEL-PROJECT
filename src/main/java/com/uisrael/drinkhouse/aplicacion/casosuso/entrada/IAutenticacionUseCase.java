package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Usuario;

public interface IAutenticacionUseCase {

	/**
	 * Valida email + password contra la base de datos.
	 *
	 * @return el usuario autenticado (con negocioId/rolId poblados) si las
	 *         credenciales son correctas y la cuenta esta activa; vacio en
	 *         cualquier otro caso (usuario no existe, password incorrecto,
	 *         cuenta inactiva).
	 */
	Optional<Usuario> autenticar(String email, String password);

}
