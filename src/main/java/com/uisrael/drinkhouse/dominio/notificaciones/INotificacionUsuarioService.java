package com.uisrael.drinkhouse.dominio.notificaciones;

/**
 * Puerto de salida para el envio de notificaciones al usuario relacionadas
 * con la seguridad de su cuenta (por ejemplo, codigos de recuperacion de
 * contrasena). La infraestructura concreta (correo electronico, SMS, etc.)
 * se implementa en la capa de infraestructura.
 */
public interface INotificacionUsuarioService {

	void enviarCodigoRecuperacionPassword(String emailDestino, String nombreDestino, String codigo,
			int minutosExpiracion);

}
