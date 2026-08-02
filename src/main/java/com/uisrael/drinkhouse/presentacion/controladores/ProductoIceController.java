package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoIceUseCase;
import com.uisrael.drinkhouse.dominio.entidades.ProductoIceHistorico;
import com.uisrael.drinkhouse.presentacion.dto.request.ProductoIceRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MensajeResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProductoIceResponseDto;

import jakarta.validation.Valid;

/**
 * El ICE varia por producto (tipo de bebida, grado alcoholico, etc.), asi
 * que se gestiona anidado bajo /api/productos/{productoId}/ice, con
 * historico completo por producto.
 */
@RestController
@RequestMapping("/api/productos/{productoId}/ice")
public class ProductoIceController {

	private final IProductoIceUseCase productoIceUseCase;

	public ProductoIceController(IProductoIceUseCase productoIceUseCase) {
		this.productoIceUseCase = productoIceUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoIceResponseDto crear(@PathVariable Long productoId,
			@Valid @RequestBody ProductoIceRequestDto requestDto) {
		ProductoIceHistorico creado = productoIceUseCase.crearNuevaTasa(productoId, requestDto.getValor(),
				requestDto.getTipoIce(), requestDto.getMotivo());
		return toResponseDto(creado);
	}

	@GetMapping
	public List<ProductoIceResponseDto> listarHistorial(@PathVariable Long productoId) {
		return productoIceUseCase.listarHistorial(productoId).stream().map(this::toResponseDto).toList();
	}

	@GetMapping("/vigente")
	public ProductoIceResponseDto obtenerVigente(@PathVariable Long productoId) {
		return productoIceUseCase.obtenerVigente(productoId).map(this::toResponseDto)
				.orElseThrow(() -> new NoSuchElementException(
						"El producto " + productoId + " no tiene una tasa de ICE configurada"));
	}

	private ProductoIceResponseDto toResponseDto(ProductoIceHistorico historico) {
		ProductoIceResponseDto dto = new ProductoIceResponseDto();
		dto.setId(historico.getId());
		dto.setProductoId(historico.getProductoId());
		dto.setTipoIce(historico.getTipoIce());
		dto.setValor(historico.getValor());
		dto.setVigenteDesde(historico.getVigenteDesde());
		dto.setVigenteHasta(historico.getVigenteHasta());
		dto.setMotivo(historico.getMotivo());
		return dto;
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<MensajeResponseDto> manejarNoEncontrado(NoSuchElementException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MensajeResponseDto(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<MensajeResponseDto> manejarValidacion(MethodArgumentNotValidException ex) {
		String mensaje = ex.getBindingResult().getFieldErrors().stream().findFirst()
				.map(error -> error.getDefaultMessage()).orElse("Datos invalidos");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeResponseDto(mensaje));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<MensajeResponseDto> manejarArgumentoInvalido(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MensajeResponseDto(ex.getMessage()));
	}
}
