package com.uisrael.drinkhouse.infraestructura.notificaciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.uisrael.drinkhouse.dominio.notificaciones.INotificacionUsuarioService;

import jakarta.mail.internet.MimeMessage;

/**
 * Adaptador de salida que envia notificaciones al usuario por correo
 * electronico usando JavaMailSender (autoconfigurado por
 * spring-boot-starter-mail a partir de spring.mail.*).
 *
 * Si el envio falla (por ejemplo, SMTP no configurado en el ambiente), el
 * error se registra pero no se propaga: el flujo de recuperacion de cuenta no
 * debe revelar detalles de infraestructura al usuario final.
 */
public class EmailNotificacionUsuarioServiceImpl implements INotificacionUsuarioService {

	private static final Logger logger = LoggerFactory.getLogger(EmailNotificacionUsuarioServiceImpl.class);

	private final JavaMailSender mailSender;
	private final String remitente;

	public EmailNotificacionUsuarioServiceImpl(JavaMailSender mailSender, String remitente) {
		this.mailSender = mailSender;
		this.remitente = remitente;
	}

	@Override
	public void enviarCodigoRecuperacionPassword(String emailDestino, String nombreDestino, String codigo,
			int minutosExpiracion) {
		try {
			MimeMessage mensaje = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, "UTF-8");
			helper.setFrom(remitente);
			helper.setTo(emailDestino);
			helper.setSubject("Drinkhouse - Codigo para recuperar tu contrasena");
			helper.setText(construirCuerpoHtml(nombreDestino, codigo, minutosExpiracion), true);

			mailSender.send(mensaje);
			logger.info("Codigo de recuperacion de password enviado a {}", emailDestino);
		} catch (MailException | jakarta.mail.MessagingException e) {
			logger.error("No se pudo enviar el correo de recuperacion de password a {}", emailDestino, e);
		}
	}

	private String construirCuerpoHtml(String nombreDestino, String codigo, int minutosExpiracion) {
		String nombre = (nombreDestino == null || nombreDestino.isBlank()) ? "" : nombreDestino;
		return """
				<div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto;">
					<h2 style="color: #667eea;">Recuperacion de contrasena</h2>
					<p>Hola %s,</p>
					<p>Recibimos una solicitud para restablecer la contrasena de tu cuenta en Drinkhouse.
					   Usa el siguiente codigo para continuar:</p>
					<p style="font-size: 28px; font-weight: bold; letter-spacing: 6px; text-align: center;
					   background: #f1f1f6; padding: 16px; border-radius: 8px;">%s</p>
					<p>Este codigo vence en %d minutos. Si tu no solicitaste este cambio, puedes ignorar este correo.</p>
					<p style="color: #6c757d; font-size: 12px;">Drinkhouse - Sistema de Gestion Integral</p>
				</div>
				"""
				.formatted(nombre, codigo, minutosExpiracion);
	}

}
