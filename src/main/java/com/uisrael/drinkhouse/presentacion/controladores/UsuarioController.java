package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IUsuarioUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.UsuarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.UsuarioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IUsuarioDtoMappper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

	private final IUsuarioUseCase usuarioUseCase;
	private final IUsuarioDtoMappper mapper;

	public UsuarioController(IUsuarioUseCase usuarioUseCase, IUsuarioDtoMappper mapper) {
		this.usuarioUseCase = usuarioUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<UsuarioResponseDto> crear(@Valid @RequestBody UsuarioRequestDto requestDto) {
		UsuarioResponseDto response = mapper.toResponseDto(
				usuarioUseCase.crearUsuario(mapper.toDomain(requestDto)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PatchMapping("/{id}/activar")
	public ResponseEntity<UsuarioResponseDto> activar(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponseDto(usuarioUseCase.activarUsuario(id)));
	}

	@PatchMapping("/{id}/desactivar")
	public ResponseEntity<UsuarioResponseDto> desactivar(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponseDto(usuarioUseCase.desactivarUsuario(id)));
	}

	@GetMapping("/{id}")
	public ResponseEntity<UsuarioResponseDto> buscarPorId(@PathVariable UUID id) {
		return ResponseEntity.ok(mapper.toResponseDto(usuarioUseCase.buscarPorId(id)));
	}

	@GetMapping
	public ResponseEntity<List<UsuarioResponseDto>> listar(
			@RequestParam(required = false) String estadoCuenta) {
		List<UsuarioResponseDto> lista = usuarioUseCase.listarConFiltro(estadoCuenta)
				.stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UsuarioResponseDto> actualizar(
			@PathVariable UUID id,
			@Valid @RequestBody UsuarioRequestDto requestDto) {
		UsuarioResponseDto response = mapper.toResponseDto(
				usuarioUseCase.actualizarUsuario(id, mapper.toDomain(requestDto)));
		return ResponseEntity.ok(response);
	}
}
