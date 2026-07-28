package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IOrdenCompraUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.OrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.OrdenCompraResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IOrdenCompraDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ordenes-compra")
public class OrdenCompraController {

	private final IOrdenCompraUseCase ordenCompraUseCase;
	private final IOrdenCompraDtoMapper mapper;

	public OrdenCompraController(IOrdenCompraUseCase ordenCompraUseCase, IOrdenCompraDtoMapper mapper) {
		this.ordenCompraUseCase = ordenCompraUseCase;
		this.mapper = mapper;
	}

	@GetMapping
	public List<OrdenCompraResponseDto> listar(@RequestParam(required = false) String estado) {
		return ordenCompraUseCase.listar(estado).stream().map(mapper::toResponseDto).toList();
	}

	@GetMapping("/{id}")
	public OrdenCompraResponseDto buscarPorId(@PathVariable Long id) {
		return mapper.toResponseDto(ordenCompraUseCase.buscarPorId(id));
	}

	@GetMapping("/by-reference/{codigo}")
	public OrdenCompraResponseDto buscarPorCodigo(@PathVariable String codigo) {
		return mapper.toResponseDto(ordenCompraUseCase.buscarPorCodigo(codigo));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public OrdenCompraResponseDto crear(@Valid @RequestBody OrdenCompraRequestDto requestDto) {
		return mapper.toResponseDto(ordenCompraUseCase.crear(mapper.toDomain(requestDto)));
	}

	@PutMapping("/{id}")
	public OrdenCompraResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody OrdenCompraRequestDto requestDto) {
		return mapper.toResponseDto(ordenCompraUseCase.actualizar(id, mapper.toDomain(requestDto)));
	}

	@PutMapping("/{id}/estado")
	public OrdenCompraResponseDto cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
		String nuevoEstado = body.get("estado");
		return mapper.toResponseDto(ordenCompraUseCase.cambiarEstado(id, nuevoEstado));
	}

	@PutMapping("/{id}/receive")
	public OrdenCompraResponseDto recibir(@PathVariable Long id) {
		return mapper.toResponseDto(ordenCompraUseCase.recibir(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		ordenCompraUseCase.eliminar(id);
		return ResponseEntity.noContent().build();
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> manejarNoEncontrado(NoSuchElementException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> manejarArgumentoInvalido(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<String> manejarEstadoInvalido(IllegalStateException ex) {
		return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
	}
}
