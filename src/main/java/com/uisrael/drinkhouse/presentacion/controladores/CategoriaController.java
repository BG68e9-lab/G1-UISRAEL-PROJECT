package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICategoriaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.CategoriaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.CategoriaResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ICategoriaDtoMapper;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

	private final ICategoriaUseCase categoriaUseCase;
	private final ICategoriaDtoMapper mapper;

	public CategoriaController(ICategoriaUseCase categoriaUseCase, ICategoriaDtoMapper mapper) {
		this.categoriaUseCase = categoriaUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoriaResponseDto guardar(@Valid @RequestBody CategoriaRequestDto requestDto) {
		return mapper.toResponseDto(categoriaUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@GetMapping
	public List<CategoriaResponseDto> listarTodo() {
		return categoriaUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer idCategoria) {
		categoriaUseCase.eliminar(idCategoria);
		return ResponseEntity.noContent().build();
	}
}