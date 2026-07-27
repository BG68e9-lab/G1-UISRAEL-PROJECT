package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRolUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.RolRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.RolResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IRolDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/roles")
public class RolController {

	private final IRolUseCase rolUseCase;
	private final IRolDtoMapper mapper;

	public RolController(IRolUseCase rolUseCase, IRolDtoMapper mapper) {
		this.rolUseCase = rolUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<RolResponseDto> crear(@Valid @RequestBody RolRequestDto requestDto) {
		RolResponseDto response = mapper.toResponseDto(rolUseCase.crearRol(mapper.toDomain(requestDto)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<RolResponseDto> actualizar(
			@PathVariable Integer id,
			@Valid @RequestBody RolRequestDto requestDto) {
		RolResponseDto response = mapper.toResponseDto(rolUseCase.actualizarRol(id, mapper.toDomain(requestDto)));
		return ResponseEntity.ok(response);
	}

	@GetMapping
	public ResponseEntity<List<RolResponseDto>> listar() {
		List<RolResponseDto> lista = rolUseCase.listarRoles().stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	@PostMapping("/{rolId}/usuarios/{usuarioId}")
	public ResponseEntity<Void> asignarRol(
			@PathVariable Integer rolId,
			@PathVariable UUID usuarioId) {
		rolUseCase.asignarRolAUsuario(usuarioId, rolId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{rolId}/usuarios/{usuarioId}")
	public ResponseEntity<Void> revocarRol(
			@PathVariable Integer rolId,
			@PathVariable UUID usuarioId) {
		rolUseCase.revocarRolDeUsuario(usuarioId, rolId);
		return ResponseEntity.noContent().build();
	}
}
