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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;
import com.uisrael.drinkhouse.presentacion.dto.request.TipoProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.TipoProductoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ITipoProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/tipos-producto")
public class TipoProductoController {

	private final ITipoProductoUseCase tipoProductoUseCase;
	private final ITipoProductoDtoMapper mapper;

	public TipoProductoController(ITipoProductoUseCase tipoProductoUseCase, ITipoProductoDtoMapper mapper) {
		this.tipoProductoUseCase = tipoProductoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<TipoProductoResponseDto> crear(@Valid @RequestBody TipoProductoRequestDto requestDto) {
		TipoProducto tipoProducto = tipoProductoUseCase.crear(mapper.toDomain(requestDto));
		return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(tipoProducto));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TipoProductoResponseDto> actualizar(
			@PathVariable Long id,
			@Valid @RequestBody TipoProductoRequestDto requestDto) {
		TipoProducto tipoProducto = mapper.toDomain(requestDto);
		tipoProducto.setTipoProductoId(id);
		TipoProducto actualizado = tipoProductoUseCase.actualizar(tipoProducto);
		return ResponseEntity.ok(mapper.toResponseDto(actualizado));
	}

	@GetMapping("/{id}")
	public ResponseEntity<TipoProductoResponseDto> buscarPorId(@PathVariable Long id) {
		return tipoProductoUseCase.buscarPorId(id)
				.map(mapper::toResponseDto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<TipoProductoResponseDto>> listar(
			@RequestParam(required = false) Long categoriaId) {
		List<TipoProducto> tipos;
		if (categoriaId != null) {
			tipos = tipoProductoUseCase.listarPorCategoria(categoriaId);
		} else {
			tipos = tipoProductoUseCase.listarTodos();
		}
		List<TipoProductoResponseDto> response = tipos.stream()
				.map(mapper::toResponseDto)
				.toList();
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		tipoProductoUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/desactivar")
	public ResponseEntity<Void> desactivar(@PathVariable Long id) {
		tipoProductoUseCase.desactivar(id);
		return ResponseEntity.noContent().build();
	}
}
