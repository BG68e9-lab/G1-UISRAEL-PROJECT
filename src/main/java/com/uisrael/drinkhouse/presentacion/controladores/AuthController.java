package com.uisrael.drinkhouse.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IUsuarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.presentacion.dto.request.LoginRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LoginResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.UsuarioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IUsuarioDtoMappper;

import jakarta.validation.Valid;

/**
 * Controlador de autenticación simple.
 * Valida email y contraseña contra la base de datos usando BCrypt.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

	private final IUsuarioUseCase usuarioUseCase;
	private final IUsuarioDtoMappper mapper;
	private final BCryptPasswordEncoder passwordEncoder;

	public AuthController(IUsuarioUseCase usuarioUseCase, IUsuarioDtoMappper mapper) {
		this.usuarioUseCase = usuarioUseCase;
		this.mapper = mapper;
		this.passwordEncoder = new BCryptPasswordEncoder();
	}

	/**
	 * POST /api/v1/auth/login
	 * Autentica un usuario con email y contraseña.
	 * 
	 * @param loginRequest email y password
	 * @return LoginResponseDto con datos del usuario si es exitoso
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
		System.out.println("=== DEBUG LOGIN ===");
		System.out.println("Email recibido: " + loginRequest.getEmail());
		System.out.println("Password recibido: " + loginRequest.getPassword());
		
		try {
			// Buscar usuario por email
			Usuario usuario = usuarioUseCase.buscarPorEmail(loginRequest.getEmail());
			System.out.println("Usuario encontrado: " + usuario.getEmail());
			System.out.println("Hash en BD: " + usuario.getPasswordHash());
			
			// Validar contraseña con BCrypt
			boolean matches = passwordEncoder.matches(loginRequest.getPassword(), usuario.getPasswordHash());
			System.out.println("¿Contraseña coincide?: " + matches);
			
			if (!matches) {
				System.out.println("ERROR: Contraseña no coincide");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new ErrorResponse("Credenciales inválidas"));
			}

			// Validar estado de cuenta
			if (!"ACTIVO".equals(usuario.getEstadoCuenta())) {
				System.out.println("ERROR: Usuario no está ACTIVO, estado: " + usuario.getEstadoCuenta());
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(new ErrorResponse("Cuenta inactiva. Contacte al administrador."));
			}

			// Construir respuesta exitosa
			UsuarioResponseDto usuarioDto = mapper.toResponseDto(usuario);
			LoginResponseDto response = new LoginResponseDto(
					"Login exitoso",
					usuarioDto
			);

			System.out.println("Login exitoso para: " + usuario.getEmail());
			return ResponseEntity.ok(response);

		} catch (Exception e) {
			System.out.println("ERROR EXCEPTION: " + e.getMessage());
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new ErrorResponse("Credenciales inválidas"));
		}
	}
	
	/**
	 * GET /api/v1/auth/generate-hash?password=xxx
	 * ENDPOINT TEMPORAL - Genera hash BCrypt para una contraseña
	 */
	@PostMapping("/generate-hash")
	public ResponseEntity<?> generateHash(@RequestBody String password) {
		String hash = passwordEncoder.encode(password);
		System.out.println("Password: " + password);
		System.out.println("Hash generado: " + hash);
		
		// Verificar que funciona
		boolean matches = passwordEncoder.matches(password, hash);
		System.out.println("Verificación: " + matches);
		
		return ResponseEntity.ok(java.util.Map.of(
			"password", password,
			"hash", hash,
			"verified", matches
		));
	}

	/**
	 * Clase interna para respuestas de error.
	 */
	private static class ErrorResponse {
		private final String message;

		public ErrorResponse(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}
}
