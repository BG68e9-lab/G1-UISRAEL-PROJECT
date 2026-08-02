package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IAutenticacionUseCase;
import com.uisrael.drinkhouse.aplicacion.excepciones.CredencialesInvalidasException;
import com.uisrael.drinkhouse.dominio.entidades.Usuario;
import com.uisrael.drinkhouse.presentacion.dto.request.LoginRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LoginResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MensajeResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.UsuarioLoginDto;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {

	private final IAutenticacionUseCase autenticacionUseCase;

	public AutenticacionController(IAutenticacionUseCase autenticacionUseCase) {
		this.autenticacionUseCase = autenticacionUseCase;
	}

	@PostMapping("/login")
	public LoginResponseDto login(@Valid @RequestBody LoginRequestDto requestDto) {
		Optional<Usuario> usuarioOpt = autenticacionUseCase.autenticar(requestDto.getEmail(), requestDto.getPassword());

		Usuario usuario = usuarioOpt
				.orElseThrow(() -> new CredencialesInvalidasException("Email o contrasena incorrectos"));

		UsuarioLoginDto usuarioDto = new UsuarioLoginDto();
		usuarioDto.setUsuarioId(usuario.getUsuarioId());
		usuarioDto.setEmail(usuario.getEmail());
		usuarioDto.setNombreCompleto(construirNombreCompleto(usuario));
		usuarioDto.setNegocioId(usuario.getNegocioId());
		usuarioDto.setRolId(usuario.getRolId());
		usuarioDto.setRolNombre(usuario.getRolNombre());

		return new LoginResponseDto("Login exitoso", usuarioDto);
	}

	private String construirNombreCompleto(Usuario usuario) {
		String nombres = usuario.getNombres() != null ? usuario.getNombres().trim() : "";
		String apellidos = usuario.getApellidos() != null ? usuario.getApellidos().trim() : "";
		String nombreCompleto = (nombres + " " + apellidos).trim();
		return nombreCompleto.isEmpty() ? usuario.getEmail() : nombreCompleto;
	}

	@ExceptionHandler(CredencialesInvalidasException.class)
	public ResponseEntity<MensajeResponseDto> manejarCredencialesInvalidas(CredencialesInvalidasException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MensajeResponseDto(ex.getMessage()));
	}

}
