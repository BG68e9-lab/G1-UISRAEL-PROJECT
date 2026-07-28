package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.ActualizarCantidadLoteRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.request.LoteProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LoteProductoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ILoteProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loteproductos")
public class LoteProductoController {

	private final ILoteProductoUseCase loteProductoUseCase;
	private final ILoteProductoDtoMapper mapper;

	public LoteProductoController(ILoteProductoUseCase loteProductoUseCase, ILoteProductoDtoMapper mapper) {
		this.loteProductoUseCase = loteProductoUseCase;
		this.mapper = mapper;
	}

	@GetMapping
	public List<LoteProductoResponseDto> listarTodo() {
		return loteProductoUseCase.listar(null).stream().map(mapper::toResponseDto).toList();
	}

	@GetMapping("/proximos-vencer")
	public List<LoteProductoResponseDto> proximosAVencer(@RequestParam(defaultValue = "7") int dias) {
		return loteProductoUseCase.listarProximosAVencer(dias).stream().map(mapper::toResponseDto).toList();
	}

	@GetMapping("/producto/{productoId}")
	public List<LoteProductoResponseDto> porProducto(@PathVariable Long productoId) {
		return loteProductoUseCase.listar(productoId).stream().map(mapper::toResponseDto).toList();
	}

	@GetMapping("/{id}")
	public LoteProductoResponseDto buscarPorId(@PathVariable Long id) {
		return mapper.toResponseDto(loteProductoUseCase.buscarPorId(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LoteProductoResponseDto guardar(@Valid @RequestBody LoteProductoRequestDto requestDto) {
		return mapper.toResponseDto(loteProductoUseCase.crear(mapper.toDomain(requestDto)));
	}

	@PutMapping("/{id}")
	public LoteProductoResponseDto actualizar(@PathVariable Long id, @Valid @RequestBody LoteProductoRequestDto requestDto) {
		return mapper.toResponseDto(loteProductoUseCase.actualizar(id, mapper.toDomain(requestDto)));
	}

	@PatchMapping("/{id}/cantidad")
	public LoteProductoResponseDto actualizarCantidad(@PathVariable Long id,
			@Valid @RequestBody ActualizarCantidadLoteRequestDto requestDto) {
		return mapper.toResponseDto(loteProductoUseCase.actualizarCantidad(id, requestDto.getCantidadDisponible()));
	}

	@PatchMapping("/{id}/activar")
	public LoteProductoResponseDto activar(@PathVariable Long id) {
		return mapper.toResponseDto(loteProductoUseCase.activar(id));
	}

	@PatchMapping("/{id}/desactivar")
	public LoteProductoResponseDto desactivar(@PathVariable Long id) {
		return mapper.toResponseDto(loteProductoUseCase.desactivar(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		loteProductoUseCase.eliminar(id);
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
