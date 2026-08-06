package com.uisrael.drinkhouse.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICodigoAccesoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IUsuarioUseCase;
import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
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
	private final ICodigoAccesoUseCase codigoAccesoUseCase;
	private final IUsuarioDtoMappper mapper;
	private final BCryptPasswordEncoder passwordEncoder;

	public AuthController(IUsuarioUseCase usuarioUseCase, ICodigoAccesoUseCase codigoAccesoUseCase, IUsuarioDtoMappper mapper) {
		this.usuarioUseCase = usuarioUseCase;
		this.codigoAccesoUseCase = codigoAccesoUseCase;
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
			Usuario usuario = usuarioUseCase.buscarPorEmail(loginRequest.getEmail());
			System.out.println("Usuario encontrado: " + usuario.getEmail());
			System.out.println("Hash en BD: " + usuario.getPasswordHash());
			
			boolean matches = passwordEncoder.matches(loginRequest.getPassword(), usuario.getPasswordHash());
			System.out.println("¿Contraseña coincide?: " + matches);
			
			if (!matches) {
				System.out.println("ERROR: Contraseña no coincide");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
						.body(new ErrorResponse("Credenciales inválidas"));
			}

			if (!"ACTIVO".equals(usuario.getEstadoCuenta())) {
				System.out.println("ERROR: Usuario no está ACTIVO, estado: " + usuario.getEstadoCuenta());
				return ResponseEntity.status(HttpStatus.FORBIDDEN)
						.body(new ErrorResponse("Cuenta inactiva. Contacte al administrador."));
			}

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
	 * Genera hash BCrypt para una contraseña (endpoint temporal)
	 */
	@PostMapping("/generate-hash")
	public ResponseEntity<?> generateHash(@RequestBody String password) {
		String hash = passwordEncoder.encode(password);
		System.out.println("Password: " + password);
		System.out.println("Hash generado: " + hash);
		
		boolean matches = passwordEncoder.matches(password, hash);
		System.out.println("Verificación: " + matches);
		
		return ResponseEntity.ok(java.util.Map.of(
			"password", password,
			"hash", hash,
			"verified", matches
		));
	}

	/**
	 * POST /api/v1/auth/solicitar-recuperacion
	 * Genera código de acceso y lo envía al email del usuario
	 * Rate limit: 1 código cada 20 minutos por email
	 */
	@PostMapping("/solicitar-recuperacion")
	public ResponseEntity<?> solicitarRecuperacion(@RequestBody java.util.Map<String, String> request) {
		String email = request.get("email");
		
		try {
			Usuario usuario = usuarioUseCase.buscarPorEmail(email);
			
			java.util.Optional<CodigoAcceso> ultimoCodigo = codigoAccesoUseCase
					.buscarUltimoCodigoPorUsuarioYTipo(usuario.getUsuarioId(), "RECUPERACION_PASS");
			
			if (ultimoCodigo.isPresent()) {
				java.time.OffsetDateTime hace20Minutos = java.time.OffsetDateTime.now().minusMinutes(20);
				if (ultimoCodigo.get().getCreadoEn().isAfter(hace20Minutos)) {
					long minutosRestantes = java.time.Duration.between(
							java.time.OffsetDateTime.now(), 
							ultimoCodigo.get().getCreadoEn().plusMinutes(20)
					).toMinutes();
					
					return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
							.body(java.util.Map.of(
								"error", "Debes esperar " + minutosRestantes + " minuto(s) antes de solicitar otro código"
							));
				}
			}
			
			codigoAccesoUseCase.generarCodigo("RECUPERACION_PASS", usuario.getUsuarioId());
			
			return ResponseEntity.ok(java.util.Map.of(
				"mensaje", "Si el email existe, recibirás un código de recuperación"
			));
		} catch (Exception e) {
			return ResponseEntity.ok(java.util.Map.of(
				"mensaje", "Si el email existe, recibirás un código de recuperación"
			));
		}
	}

	/**
	 * POST /api/v1/auth/validar-codigo-recuperacion
	 * Valida que el código sea correcto
	 */
	@PostMapping("/validar-codigo-recuperacion")
	public ResponseEntity<?> validarCodigoRecuperacion(@RequestBody java.util.Map<String, String> request) {
		String codigo = request.get("codigo");
		
		try {
			CodigoAcceso codigoAcceso = codigoAccesoUseCase.validarCodigo(codigo);
			
			if (!"RECUPERACION_PASS".equals(codigoAcceso.getTipoCodigo())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(java.util.Map.of("error", "Código no válido para recuperación"));
			}
			
			return ResponseEntity.ok(java.util.Map.of(
				"mensaje", "Código válido",
				"usuarioId", codigoAcceso.getUsuarioId().toString()
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(java.util.Map.of("error", "Código inválido o expirado"));
		}
	}

	/**
	 * POST /api/v1/auth/restablecer-contrasena
	 * Cambia la contraseña del usuario después de validar el código
	 */
	@PostMapping("/restablecer-contrasena")
	public ResponseEntity<?> restablecerContrasena(@RequestBody java.util.Map<String, String> request) {
		String codigo = request.get("codigo");
		String nuevaPassword = request.get("nuevaPassword");
		
		try {
			CodigoAcceso codigoAcceso = codigoAccesoUseCase.validarCodigo(codigo);
			
			if (!"RECUPERACION_PASS".equals(codigoAcceso.getTipoCodigo())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(java.util.Map.of("error", "Código no válido"));
			}
			
			Usuario usuario = usuarioUseCase.buscarPorId(codigoAcceso.getUsuarioId());
			
			String hashNuevo = passwordEncoder.encode(nuevaPassword);
			usuario.setPasswordHash(hashNuevo);
			usuarioUseCase.actualizarUsuario(codigoAcceso.getUsuarioId(), usuario);
			
			return ResponseEntity.ok(java.util.Map.of(
				"mensaje", "Contraseña actualizada exitosamente"
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(java.util.Map.of("error", "Error al restablecer contraseña: " + e.getMessage()));
		}
	}

	/**
	 * POST /api/v1/auth/solicitar-codigo-movimientos
	 * Genera código de acceso para movimientos de inventario y lo envía al email del usuario
	 * Rate limit: 1 código cada 2 minutos por email
	 */
	@PostMapping("/solicitar-codigo-movimientos")
	public ResponseEntity<?> solicitarCodigoMovimientos(@RequestBody java.util.Map<String, String> request) {
		String email = request.get("email");
		
		try {
			Usuario usuario = usuarioUseCase.buscarPorEmail(email);
			
			java.util.Optional<CodigoAcceso> ultimoCodigo = codigoAccesoUseCase
					.buscarUltimoCodigoPorUsuarioYTipo(usuario.getUsuarioId(), "MOV_INVENTARIO");
			
			if (ultimoCodigo.isPresent()) {
				java.time.OffsetDateTime hace2Minutos = java.time.OffsetDateTime.now().minusMinutes(2);
				if (ultimoCodigo.get().getCreadoEn().isAfter(hace2Minutos)) {
					long segundosRestantes = java.time.Duration.between(
							java.time.OffsetDateTime.now(), 
							ultimoCodigo.get().getCreadoEn().plusMinutes(2)
					).getSeconds();
					
					return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
							.body(java.util.Map.of(
								"error", "Debes esperar " + segundosRestantes + " segundo(s) antes de solicitar otro código"
							));
				}
			}
			
			codigoAccesoUseCase.generarCodigo("MOV_INVENTARIO", usuario.getUsuarioId());
			
			return ResponseEntity.ok(java.util.Map.of(
				"mensaje", "Código de verificación enviado a tu correo electrónico"
			));
		} catch (Exception e) {
			return ResponseEntity.ok(java.util.Map.of(
				"mensaje", "Si el email existe, recibirás un código de verificación"
			));
		}
	}

	/**
	 * POST /api/v1/auth/validar-codigo-movimientos
	 * Valida que el código sea correcto para acceso a movimientos.
	 * NO MARCA EL CÓDIGO COMO USADO - Se puede llamar múltiples veces.
	 */
	@PostMapping("/validar-codigo-movimientos")
	public ResponseEntity<?> validarCodigoMovimientos(@RequestBody java.util.Map<String, String> request) {
		String codigo = request.get("codigo");
		
		try {
			CodigoAcceso codigoAcceso = codigoAccesoUseCase.validarCodigoSinMarcar(codigo);
			
			if (!"MOV_INVENTARIO".equals(codigoAcceso.getTipoCodigo())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(java.util.Map.of("error", "Código no válido para movimientos de inventario"));
			}
			
			return ResponseEntity.ok(java.util.Map.of(
				"valid", true,
				"message", "Código válido",
				"usuarioId", codigoAcceso.getUsuarioId().toString(),
				"codigoAccesoId", codigoAcceso.getCodigoAccesoId().toString()
			));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(java.util.Map.of("error", "Código inválido o expirado"));
		}
	}

	/**
	 * POST /api/v1/auth/marcar-codigo-usado
	 * Marca un código como usado cuando el usuario sale de la sección de movimientos.
	 */
	@PostMapping("/marcar-codigo-usado")
	public ResponseEntity<?> marcarCodigoUsado(@RequestBody java.util.Map<String, String> request) {
		String codigo = request.get("codigo");
		
		try {
			CodigoAcceso codigoAcceso = codigoAccesoUseCase.marcarCodigoComoUsado(codigo);
			
			return ResponseEntity.ok(java.util.Map.of(
				"message", "Código marcado como usado",
				"codigoAccesoId", codigoAcceso.getCodigoAccesoId().toString()
			));
		} catch (com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(java.util.Map.of("error", "Código no encontrado"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(java.util.Map.of("error", "Error al marcar código: " + e.getMessage()));
		}
	}

	/**
	 * GET /api/v1/auth/verificar-acceso-movimientos?email=xxx
	 * Verifica si el usuario tiene un código válido de movimientos SIN marcarlo como usado.
	 * Útil para verificar si ya tiene acceso activo.
	 */
	@GetMapping("/verificar-acceso-movimientos")
	public ResponseEntity<?> verificarAccesoMovimientos(@RequestParam String email) {
		try {
			Usuario usuario = usuarioUseCase.buscarPorEmail(email);
			
			java.util.Optional<CodigoAcceso> ultimoCodigo = codigoAccesoUseCase
					.buscarUltimoCodigoPorUsuarioYTipo(usuario.getUsuarioId(), "MOV_INVENTARIO");
			
			if (ultimoCodigo.isEmpty()) {
				return ResponseEntity.ok(java.util.Map.of(
					"tieneAcceso", false,
					"mensaje", "No hay código de verificación"
				));
			}
			
			CodigoAcceso codigo = ultimoCodigo.get();
			
			if (Boolean.TRUE.equals(codigo.getUsado()) && 
			    codigo.getExpiraEn() != null && 
			    codigo.getExpiraEn().isAfter(java.time.OffsetDateTime.now())) {
				return ResponseEntity.ok(java.util.Map.of(
					"tieneAcceso", true,
					"mensaje", "Acceso activo",
					"usuarioId", usuario.getUsuarioId().toString(),
					"codigoAccesoId", codigo.getCodigoAccesoId().toString()
				));
			} else {
				return ResponseEntity.ok(java.util.Map.of(
					"tieneAcceso", false,
					"mensaje", "Código no válido o expirado"
				));
			}
		} catch (Exception e) {
			return ResponseEntity.ok(java.util.Map.of(
				"tieneAcceso", false,
				"mensaje", "No se pudo verificar acceso"
			));
		}
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
