package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.MovimientoInventarioRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MovimientoInventarioResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IMovimientoInventarioDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/movimientos-inventario")
public class MovimientoInventarioController {

	private final IMovimientoInventarioUseCase movimientoUseCase;
	private final IMovimientoInventarioDtoMapper mapper;

	public MovimientoInventarioController(IMovimientoInventarioUseCase movimientoUseCase, IMovimientoInventarioDtoMapper mapper) {
		this.movimientoUseCase = movimientoUseCase;
		this.mapper = mapper;
	}

	@GetMapping
	public Page<MovimientoInventarioResponseDto> listar(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(required = false) String tipo) {
		List<MovimientoInventarioResponseDto> todos = movimientoUseCase.listarTodo(tipo).stream()
				.map(mapper::toResponseDto)
				.toList();

		int desde = Math.min(page * size, todos.size());
		int hasta = Math.min(desde + size, todos.size());
		List<MovimientoInventarioResponseDto> pagina = todos.subList(desde, hasta);

		return new PageImpl<>(pagina, PageRequest.of(page, size), todos.size());
	}

	@GetMapping("/{id}")
	public MovimientoInventarioResponseDto buscarPorId(@PathVariable Long id) {
		return mapper.toResponseDto(movimientoUseCase.buscarPorId(id));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public MovimientoInventarioResponseDto guardar(@Valid @RequestBody MovimientoInventarioRequestDto requestDto) {
		return mapper.toResponseDto(movimientoUseCase.guardar(mapper.toDomain(requestDto)));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		movimientoUseCase.eliminar(id);
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
