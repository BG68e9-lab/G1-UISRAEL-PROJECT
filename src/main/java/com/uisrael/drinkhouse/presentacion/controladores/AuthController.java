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
