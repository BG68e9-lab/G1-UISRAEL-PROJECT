package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRecuperacionCuentaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.dominio.notificaciones.INotificacionUsuarioService;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;

public class RecuperacionCuentaUseCaseImpl implements IRecuperacionCuentaUseCase {

	// OJO: la columna codigos_acceso.tipo_codigo es VARCHAR(20); no alargar este valor.
	public static final String TIPO_CODIGO_RECUPERACION_PASSWORD = "RECUPERACION_PWD";

	private static final SecureRandom RANDOM = new SecureRandom();

	private final IUsuarioRepositorio usuarioRepositorio;
	private final ICodigoAccesoRepositorio codigoAccesoRepositorio;
	private final INotificacionUsuarioService notificacionUsuarioService;
	private final PasswordEncoder passwordEncoder;
	private final int minutosExpiracionCodigo;

	public RecuperacionCuentaUseCaseImpl(IUsuarioRepositorio usuarioRepositorio,
			ICodigoAccesoRepositorio codigoAccesoRepositorio, INotificacionUsuarioService notificacionUsuarioService,
			PasswordEncoder passwordEncoder, int minutosExpiracionCodigo) {
		this.usuarioRepositorio = usuarioRepositorio;
		this.codigoAccesoRepositorio = codigoAccesoRepositorio;
		this.notificacionUsuarioService = notificacionUsuarioService;
		this.passwordEncoder = passwordEncoder;
		this.minutosExpiracionCodigo = minutosExpiracionCodigo;
	}

	@Override
	public void solicitarRecuperacionPassword(String email) {
		if (email == null || email.isBlank()) {
			return;
		}

		Optional<Usuario> usuarioOpt = usuarioRepositorio.buscarPorEmail(email.trim());
		if (usuarioOpt.isEmpty()) {
			// No revelamos si el email existe o no.
			return;
		}
		Usuario usuario = usuarioOpt.get();
		OffsetDateTime ahora = OffsetDateTime.now();

		invalidarCodigosVigentes(usuario.getUsuarioId(), ahora);

		String codigo = generarCodigoNumerico();

		CodigoAcceso nuevoCodigo = new CodigoAcceso();
		nuevoCodigo.setUsuarioId(usuario.getUsuarioId());
		nuevoCodigo.setTipoCodigo(TIPO_CODIGO_RECUPERACION_PASSWORD);
		nuevoCodigo.setCodigoHash(passwordEncoder.encode(codigo));
		nuevoCodigo.setExpiraEn(ahora.plusMinutes(minutosExpiracionCodigo));
		nuevoCodigo.setUsado(false);
		codigoAccesoRepositorio.guardar(nuevoCodigo);

		notificacionUsuarioService.enviarCodigoRecuperacionPassword(usuario.getEmail(), usuario.getNombres(), codigo,
				minutosExpiracionCodigo);
	}

	@Override
	public void restablecerPassword(String email, String codigo, String nuevaPassword) {
		if (email == null || codigo == null || nuevaPassword == null || nuevaPassword.isBlank()) {
			throw new IllegalArgumentException("Codigo invalido o expirado");
		}

		Usuario usuario = usuarioRepositorio.buscarPorEmail(email.trim())
				.orElseThrow(() -> new IllegalArgumentException("Codigo invalido o expirado"));

		OffsetDateTime ahora = OffsetDateTime.now();
		List<CodigoAcceso> vigentes = codigoAccesoRepositorio
				.buscarVigentesPorUsuarioYTipo(usuario.getUsuarioId(), TIPO_CODIGO_RECUPERACION_PASSWORD);

		CodigoAcceso codigoValido = vigentes.stream()
				.filter(c -> c.getExpiraEn() != null && c.getExpiraEn().isAfter(ahora))
				.filter(c -> passwordEncoder.matches(codigo, c.getCodigoHash()))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Codigo invalido o expirado"));

		codigoValido.setUsado(true);
		codigoValido.setUsadoEn(ahora);
		codigoAccesoRepositorio.guardar(codigoValido);

		usuarioRepositorio.actualizarPasswordHash(usuario.getUsuarioId(), passwordEncoder.encode(nuevaPassword));
	}

	private void invalidarCodigosVigentes(java.util.UUID usuarioId, OffsetDateTime ahora) {
		List<CodigoAcceso> vigentes = codigoAccesoRepositorio.buscarVigentesPorUsuarioYTipo(usuarioId,
				TIPO_CODIGO_RECUPERACION_PASSWORD);
		for (CodigoAcceso codigo : vigentes) {
			codigo.setUsado(true);
			codigo.setUsadoEn(ahora);
			codigoAccesoRepositorio.guardar(codigo);
		}
	}

	private String generarCodigoNumerico() {
		int numero = RANDOM.nextInt(1_000_000);
		return String.format("%06d", numero);
	}

}
