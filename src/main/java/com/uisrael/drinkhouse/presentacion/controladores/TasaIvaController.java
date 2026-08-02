package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITasaIvaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.TasaIva;
import com.uisrael.drinkhouse.presentacion.dto.request.TasaIvaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MensajeResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.TasaIvaResponseDto;

import jakarta.validation.Valid;

/**
 * El IVA es una tasa global (no por producto): el gobierno la fija y aplica
 * a todo el negocio, salvo productos marcados como exentos. Este controlador
 * permite registrar nuevas tasas (cerrando la anterior automaticamente) y
 * consultar el historico completo.
 */
@RestController
@RequestMapping("/api/tasas-iva")
public class TasaIvaController {

	private final ITasaIvaUseCase tasaIvaUseCase;

	public TasaIvaController(ITasaIvaUseCase tasaIvaUseCase) {
		this.tasaIvaUseCase = tasaIvaUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TasaIvaResponseDto crear(@Valid @RequestBody TasaIvaRequestDto requestDto) {
		TasaIva creada = tasaIvaUseCase.crearNuevaTasa(requestDto.getPorcentaje(), requestDto.getMotivo());
		return toResponseDto(creada);
	}

	@GetMapping
	public List<TasaIvaResponseDto> listarHistorial() {
		return tasaIvaUseCase.listarHistorial().stream().map(this::toResponseDto).toList();
	}

	@GetMapping("/vigente")
	public TasaIvaResponseDto obtenerVigente() {
		return tasaIvaUseCase.obtenerVigente().map(this::toResponseDto)
				.orElseThrow(() -> new NoSuchElementException("No hay una tasa de IVA configurada todavia"));
	}

	private TasaIvaResponseDto toResponseDto(TasaIva tasaIva) {
		TasaIvaResponseDto dto = new TasaIvaResponseDto();
		dto.setTasaIvaId(tasaIva.getTasaIvaId());
		dto.setPorcentaje(tasaIva.getPorcentaje());
		dto.setVigenteDesde(tasaIva.getVigenteDesde());
		dto.setVigenteHasta(tasaIva.getVigenteHasta());
		dto.setMotivo(tasaIva.getMotivo());
		return dto;
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<MensajeResponseDto> manejarNoEncontrado(NoSuchElementException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDto(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MensajeResponseDto> manejarValidacion(MethodArgumentNotValidException ex) {
		String mensaje = ex.getBindingResult().getFieldErrors().stream().findFirst()
				.map(error -> error.getDefaultMessage()).orElse("Datos invalidos");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeResponseDto(mensaje));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<MensajeResponseDto> manejarArgumentoInvalido(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeResponseDto(ex.getMessage()));
	}
}
