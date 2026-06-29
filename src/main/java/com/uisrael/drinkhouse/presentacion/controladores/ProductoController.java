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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.ProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProductoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final IProductoUseCase productoUseCase;
	private final IProductoDtoMapper mapper;

	public ProductoController(IProductoUseCase productoUseCase, IProductoDtoMapper mapper) {
		super();
		this.productoUseCase = productoUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponseDto guardar(@Valid @RequestBody ProductoRequestDto productoRequestDto) {
		return mapper.toResponseDto(productoUseCase. crear(mapper.toDomain(productoRequestDto)));
	}
	
	@GetMapping
	public List<ProductoResponseDto> listarTodo(){ 
		return productoUseCase. listar().stream().map(mapper :: toResponseDto). toList();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar (@PathVariable int idGrupo){
		
		productoUseCase.eliminar(idGrupo);
		return ResponseEntity.noContent().build();
	}
	

}
