package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAutenticacionUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.dominio.repositorios.IUsuarioRepositorio;

public class AutenticacionUseCaseImpl implements IAutenticacionUseCase {

	private static final String ESTADO_ACTIVO = "ACTIVO";

	private final IUsuarioRepositorio usuarioRepositorio;
	private final PasswordEncoder passwordEncoder;

	public AutenticacionUseCaseImpl(IUsuarioRepositorio usuarioRepositorio, PasswordEncoder passwordEncoder) {
		this.usuarioRepositorio = usuarioRepositorio;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public Optional<Usuario> autenticar(String email, String password) {
		if (email == null || email.isBlank() || password == null || password.isEmpty()) {
			return Optional.empty();
		}

		Optional<Usuario> usuarioOpt = usuarioRepositorio.buscarPorEmail(email.trim());
		if (usuarioOpt.isEmpty()) {
			return Optional.empty();
		}

		Usuario usuario = usuarioOpt.get();

		if (usuario.getEstadoCuenta() != null && !ESTADO_ACTIVO.equalsIgnoreCase(usuario.getEstadoCuenta())) {
			return Optional.empty();
		}

		String hashGuardado = usuario.getPasswordHash();
		if (hashGuardado == null || hashGuardado.isBlank()) {
			// Cuenta sin password local (por ejemplo, solo SSO): no se puede autenticar asi.
			return Optional.empty();
		}

		boolean credencialesValidas = esHashBCrypt(hashGuardado) ? passwordEncoder.matches(password, hashGuardado)
				: hashGuardado.equals(password);

		return credencialesValidas ? Optional.of(usuario) : Optional.empty();
	}

	/**
	 * Los hashes de BCrypt siempre empiezan con uno de estos prefijos de
	 * version. Cualquier otro valor se trata como password en texto plano
	 * (dato historico de cuando aun no se hasheaban las contrasenas).
	 */
	private boolean esHashBCrypt(String hash) {
		return hash.startsWith("$2a$") || hash.startsWith("$2b$") || hash.startsWith("$2y$");
	}

}
