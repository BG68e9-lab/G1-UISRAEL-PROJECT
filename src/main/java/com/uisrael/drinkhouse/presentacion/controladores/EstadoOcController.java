package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IEstadoOcUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.EstadoOcRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.EstadoOcResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IEstadoOcDtoMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/estados-oc")
public class EstadoOcController {

	private final IEstadoOcUseCase estadoOcUseCase;
	private final IEstadoOcDtoMapper mapper;

	public EstadoOcController(IEstadoOcUseCase estadoOcUseCase, IEstadoOcDtoMapper mapper) {
		this.estadoOcUseCase = estadoOcUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EstadoOcResponseDto guardar(@Valid @RequestBody EstadoOcRequestDto requestDto) {
		return mapper.toResponseDto(estadoOcUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<EstadoOcResponseDto> listarTodo() {
		return estadoOcUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer idEstadoOc) {
		estadoOcUseCase.eliminar(idEstadoOc);
		return ResponseEntity.noContent().build();
	}
}