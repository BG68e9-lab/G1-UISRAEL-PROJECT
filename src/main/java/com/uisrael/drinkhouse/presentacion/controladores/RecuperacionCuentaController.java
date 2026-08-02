package com.uisrael.drinkhouse.presentacion.controladores;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRecuperacionCuentaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.RestablecerPasswordRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.request.SolicitarRecuperacionRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MensajeResponseDto;

import jakarta.validation.Valid;

/**
 * Flujo de "olvide mi contrasena": solicitar un codigo de un solo uso por
 * correo y luego usarlo para establecer una nueva contrasena.
 */
@RestController
@RequestMapping("/api/auth")
public class RecuperacionCuentaController {

	private static final String MENSAJE_GENERICO_SOLICITUD = "Si el correo esta registrado, se envio un codigo de verificacion.";

	private final IRecuperacionCuentaUseCase recuperacionCuentaUseCase;

	public RecuperacionCuentaController(IRecuperacionCuentaUseCase recuperacionCuentaUseCase) {
		this.recuperacionCuentaUseCase = recuperacionCuentaUseCase;
	}

	@PostMapping("/recuperar-password")
	public MensajeResponseDto solicitarRecuperacion(@Valid @RequestBody SolicitarRecuperacionRequestDto requestDto) {
		recuperacionCuentaUseCase.solicitarRecuperacionPassword(requestDto.getEmail());
		// Respuesta identica exista o no el correo, para no revelar cuentas registradas.
		return new MensajeResponseDto(MENSAJE_GENERICO_SOLICITUD);
	}

	@PostMapping("/restablecer-password")
	public MensajeResponseDto restablecerPassword(@Valid @RequestBody RestablecerPasswordRequestDto requestDto) {
		recuperacionCuentaUseCase.restablecerPassword(requestDto.getEmail(), requestDto.getCodigo(),
				requestDto.getNuevaPassword());
		return new MensajeResponseDto("Contrasena actualizada correctamente.");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<MensajeResponseDto> manejarCodigoInvalido(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeResponseDto(ex.getMessage()));
	}

}
