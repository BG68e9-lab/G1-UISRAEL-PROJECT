package com.uisrael.drinkhouse.infraestructura.servicios;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.UsuarioEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.IUsuarioJpaRepositorio;

@Service
public class EmailService {

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	private final JavaMailSender mailSender;
	private final IUsuarioJpaRepositorio usuarioRepositorio;

	@Value("${spring.mail.username:noreply@drinkhouse.com}")
	private String fromEmail;

	public EmailService(JavaMailSender mailSender, IUsuarioJpaRepositorio usuarioRepositorio) {
		this.mailSender = mailSender;
		this.usuarioRepositorio = usuarioRepositorio;
	}

	public void enviarCodigoAcceso(UUID usuarioId, String codigo, String tipoCodigo) {
		try {
			UsuarioEntity usuario = usuarioRepositorio.findById(usuarioId)
					.orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

			if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
				logger.error("Usuario {} no tiene email configurado", usuarioId);
				throw new IllegalStateException("El usuario no tiene email configurado");
			}

			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromEmail);
			message.setTo(usuario.getEmail());
			
			if ("MOV_INVENTARIO".equals(tipoCodigo)) {
				message.setSubject("Código de verificación - Movimientos de Inventario");
				message.setText(construirMensajeMovimientos(usuario.getNombreCompleto(), codigo));
			} else if ("RECUPERACION_PASS".equals(tipoCodigo)) {
				message.setSubject("Código de recuperación de contraseña - DrinkHouse");
				message.setText(construirMensajeRecuperacion(usuario.getNombreCompleto(), codigo));
			} else {
				message.setSubject("Tu código de acceso - DrinkHouse");
				message.setText(construirMensajeCodigoAcceso(usuario.getNombreCompleto(), codigo));
			}

			mailSender.send(message);

			logger.info("Código tipo {} enviado al email {} para usuario {}", 
					tipoCodigo, usuario.getEmail(), usuarioId);

		} catch (Exception e) {
			logger.error("Error al enviar email a usuario {}: {}", usuarioId, e.getMessage());
			throw new RuntimeException("No se pudo enviar el código por correo: " + e.getMessage(), e);
		}
	}
	
	public void enviarCodigoAcceso(UUID usuarioId, String codigo) {
		enviarCodigoAcceso(usuarioId, codigo, "GENERAL");
	}

	private String construirMensajeMovimientos(String nombreUsuario, String codigo) {
		return String.format("""
				Hola %s,
				
				Has solicitado acceso a los movimientos de inventario.
				
				Tu código de verificación es:
				
				%s
				
				⚠️ Este código expira en 10 minutos y solo se puede usar una vez.
				
				Si no solicitaste este código, ignora este mensaje.
				
				---
				DrinkHouse - Sistema de Gestión
				""", nombreUsuario, codigo);
	}

	private String construirMensajeRecuperacion(String nombreUsuario, String codigo) {
		return String.format("""
				Hola %s,
				
				Has solicitado recuperar tu contraseña.
				
				Tu código de recuperación es:
				
				%s
				
				⚠️ Este código expira en 10 minutos y solo se puede usar una vez.
				
				Si no solicitaste este código, ignora este mensaje y tu contraseña permanecerá sin cambios.
				
				---
				DrinkHouse - Sistema de Gestión
				""", nombreUsuario, codigo);
	}

	private String construirMensajeCodigoAcceso(String nombreUsuario, String codigo) {
		return String.format("""
				Hola %s,
				
				Tu código de acceso es:
				
				%s
				
				Este código es válido por 24 horas.
				
				Si no solicitaste este código, ignora este mensaje.
				
				---
				DrinkHouse - Sistema de Gestión
				""", nombreUsuario, codigo);
	}
}
