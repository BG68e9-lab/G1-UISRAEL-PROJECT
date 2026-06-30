package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IUsuarioUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.UsuarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.UsuarioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IUsuarioDtoMappper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

	private final IUsuarioUseCase usuarioUseCase;
	private final IUsuarioDtoMappper mapper;

	public UsuarioController(IUsuarioUseCase usuarioUseCase, IUsuarioDtoMappper mapper) {
		this.usuarioUseCase = usuarioUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UsuarioResponseDto guardar(@Valid @RequestBody UsuarioRequestDto requestDto) {
		return mapper.toResponseDto(usuarioUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<UsuarioResponseDto> listarTodo() {
		return usuarioUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") UUID idUsuario) {
		usuarioUseCase.eliminar(idUsuario);
		return ResponseEntity.noContent().build();
	}
}