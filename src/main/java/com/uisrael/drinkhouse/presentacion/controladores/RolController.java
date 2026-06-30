package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IRolUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.RolRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.RolResponsetDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IRolDtoMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
public class RolController {

	private final IRolUseCase rolUseCase;
	private final IRolDtoMapper mapper;

	public RolController(IRolUseCase rolUseCase, IRolDtoMapper mapper) {
		this.rolUseCase = rolUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public RolResponsetDto guardar(@Valid @RequestBody RolRequestDto requestDto) {
		return mapper.toResponseDto(rolUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<RolResponsetDto> listarTodo() {
		return rolUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer idRol) {
		rolUseCase.eliminar(idRol);
		return ResponseEntity.noContent().build();
	}
}