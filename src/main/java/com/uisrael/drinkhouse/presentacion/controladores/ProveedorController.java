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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.ProveedorRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProductoResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProveedorResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IProveedorDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {
	
	private final IProveedorUseCase proveedorUseCase;
	private final IProveedorDtoMapper mapper;
	public ProveedorController(IProveedorUseCase proveedorUseCase, IProveedorDtoMapper mapper) {
		super();
		this.proveedorUseCase = proveedorUseCase;
		this.mapper = mapper;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProveedorResponseDto guardar(@Valid @RequestBody ProveedorRequestDto proveedorRequestDto) {
		
		return mapper.toResponseDto(proveedorUseCase. crear(mapper.toDomain(proveedorRequestDto)));
		
	}
	
	@GetMapping
	public List<ProveedorResponseDto> listarTodo(){
		
		return proveedorUseCase.listar().stream().map(mapper::toResponseDto).toList();
	}
	
	@DeleteMapping
	
	public ResponseEntity<Void> eliminar (@PathVariable int idProveedor){
		
		proveedorUseCase.eliminar(idProveedor);
		return ResponseEntity.noContent().build();
		
	}

}
