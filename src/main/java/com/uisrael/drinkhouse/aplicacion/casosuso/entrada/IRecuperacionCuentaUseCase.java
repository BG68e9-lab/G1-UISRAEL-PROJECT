package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

public interface IRecuperacionCuentaUseCase {

	/**
	 * Genera y envia (por correo) un codigo de un solo uso para restablecer la
	 * contrasena del usuario asociado al email. Por seguridad, si el email no
	 * esta registrado el metodo no lanza error ni revela esa informacion: no
	 * hace nada.
	 */
	void solicitarRecuperacionPassword(String email);

	/**
	 * Valida el codigo enviado previamente y, si es correcto y no ha expirado,
	 * establece la nueva contrasena del usuario.
	 *
	 * @throws RuntimeException si el codigo es invalido, ya fue usado o expiro
	 */
	void restablecerPassword(String email, String codigo, String nuevaPassword);

}
