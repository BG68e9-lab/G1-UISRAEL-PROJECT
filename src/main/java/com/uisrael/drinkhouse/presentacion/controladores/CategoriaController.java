package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ICategoriaUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.CategoriaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.CategoriaResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ICategoriaDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/categorias")
public class CategoriaController {

	private final ICategoriaUseCase categoriaUseCase;
	private final ICategoriaDtoMapper mapper;

	public CategoriaController(ICategoriaUseCase categoriaUseCase, ICategoriaDtoMapper mapper) {
		this.categoriaUseCase = categoriaUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<CategoriaResponseDto> crear(@Valid @RequestBody CategoriaRequestDto requestDto) {
		CategoriaResponseDto response = mapper.toResponseDto(
				categoriaUseCase.crearCategoria(mapper.toDomain(requestDto)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoriaResponseDto> actualizar(
			@PathVariable Long id,
			@Valid @RequestBody CategoriaRequestDto requestDto) {
		CategoriaResponseDto response = mapper.toResponseDto(
				categoriaUseCase.actualizarCategoria(id, mapper.toDomain(requestDto)));
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoriaResponseDto> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(mapper.toResponseDto(categoriaUseCase.buscarPorId(id)));
	}

	@GetMapping
	public ResponseEntity<List<CategoriaResponseDto>> listar() {
		List<CategoriaResponseDto> lista = categoriaUseCase.listarCategorias()
				.stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		categoriaUseCase.eliminarCategoria(id);
		return ResponseEntity.noContent().build();
	}
}
