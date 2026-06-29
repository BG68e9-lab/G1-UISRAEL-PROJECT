package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.LoteProductoRequestDto;

import com.uisrael.drinkhouse.presentacion.dto.response.LoteProductoResponseDto;

import com.uisrael.drinkhouse.presentacion.mapeadores.ILoteProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/loteproductos")
public class LoteProductoController {

	private final ILoteProductoUseCase loteproductoUseCase;
	private final ILoteProductoDtoMapper mapper;

	public LoteProductoController(ILoteProductoUseCase loteproductoUseCase, ILoteProductoDtoMapper mapper) {
		super();
		this.loteproductoUseCase = loteproductoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LoteProductoResponseDto guardar(@Valid @RequestBody LoteProductoRequestDto loteProductoRequestDto) {

		return mapper.toResponseDto(loteproductoUseCase.guardar(mapper.toDomain(loteProductoRequestDto)));
	}

	@GetMapping
	public List<LoteProductoResponseDto> listarTodo() {

		return loteproductoUseCase.listarTodos().stream().map(mapper::toResponseDto).toList();
	}

	@DeleteMapping
	public ResponseEntity<Void> eliminar(@PathVariable int idLoteProducto) {

		loteproductoUseCase.eliminar(idLoteProducto);
		return ResponseEntity.noContent().build();
	}

}
