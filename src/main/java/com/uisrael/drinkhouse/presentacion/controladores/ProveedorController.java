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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.presentacion.dto.request.ProveedorRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProveedorResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IProveedorDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/proveedores")
public class ProveedorController {

	private final IProveedorUseCase proveedorUseCase;
	private final IProveedorDtoMapper mapper;

	public ProveedorController(IProveedorUseCase proveedorUseCase, IProveedorDtoMapper mapper) {
		this.proveedorUseCase = proveedorUseCase;
		this.mapper = mapper;
	}

	@PostMapping
	public ResponseEntity<ProveedorResponseDto> crear(@Valid @RequestBody ProveedorRequestDto requestDto) {
		var proveedor = mapper.toDomain(requestDto);
		// TODO: Obtener negocioId del usuario autenticado cuando se implemente JWT
		proveedor.setNegocioId(1);
		
		ProveedorResponseDto response = mapper.toResponseDto(proveedorUseCase.crearProveedor(proveedor));
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ProveedorResponseDto> actualizar(
			@PathVariable Long id,
			@Valid @RequestBody ProveedorRequestDto requestDto) {
		var proveedor = mapper.toDomain(requestDto);
		// TODO: Obtener negocioId del usuario autenticado cuando se implemente JWT
		proveedor.setNegocioId(1);
		
		ProveedorResponseDto response = mapper.toResponseDto(proveedorUseCase.actualizarProveedor(id, proveedor));
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProveedorResponseDto> buscarPorId(@PathVariable Long id) {
		return ResponseEntity.ok(mapper.toResponseDto(proveedorUseCase.buscarPorId(id)));
	}

	@GetMapping
	public ResponseEntity<List<ProveedorResponseDto>> listar() {
		List<ProveedorResponseDto> lista = proveedorUseCase.listarProveedores()
				.stream().map(mapper::toResponseDto).toList();
		return ResponseEntity.ok(lista);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		proveedorUseCase.eliminarProveedor(id);
		return ResponseEntity.noContent().build();
	}
}
