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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.ProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProductoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

	private final IProductoUseCase productoUseCase;
	private final IProductoDtoMapper mapper;

	public ProductoController(IProductoUseCase productoUseCase, IProductoDtoMapper mapper) {
		this.productoUseCase = productoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<ProductoResponseDto> crear(@Valid @RequestBody ProductoRequestDto requestDto) {
		ProductoResponseDto response = mapper.toResponseDto(
				productoUseCase.crearProducto(mapper.toDomain(requestDto)));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProductoResponseDto> actualizar(
			@PathVariable Long id,
			@Valid @RequestBody ProductoRequestDto requestDto) {
		ProductoResponseDto response = mapper.toResponseDto(
				productoUseCase.actualizarProducto(id, mapper.toDomain(requestDto)));
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductoResponseDto> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(mapper.toResponseDto(productoUseCase.buscarPorId(id)));
	}

	@GetMapping
	public ResponseEntity<List<ProductoResponseDto>> listar() {
		List<ProductoResponseDto> lista = productoUseCase.listarProductos()
				.stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	@GetMapping("/buscar")
	public ResponseEntity<List<ProductoResponseDto>> buscarConFiltros(
			@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String marca,
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) Long categoriaId) {
		List<ProductoResponseDto> lista = productoUseCase
				.buscarConFiltros(nombre, marca, tipo, categoriaId)
				.stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		productoUseCase.eliminarProducto(id);
		return ResponseEntity.noContent().build();
	}
}
