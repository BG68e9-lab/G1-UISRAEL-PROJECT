package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoPrecioHistoricoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.ProductoPrecioHistorico;
import com.uisrael.drinkhouse.presentacion.dto.request.ProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.MensajeResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProductoPrecioHistoricoResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ProductoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

	private final IProductoUseCase productoUseCase;
	private final IProductoDtoMapper mapper;
	private final IProductoPrecioHistoricoUseCase precioHistoricoUseCase;

	public ProductoController(IProductoUseCase productoUseCase, IProductoDtoMapper mapper,
			IProductoPrecioHistoricoUseCase precioHistoricoUseCase) {
		super();
		this.productoUseCase = productoUseCase;
		this.mapper = mapper;
		this.precioHistoricoUseCase = precioHistoricoUseCase;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public ProductoResponseDto guardar(@Valid @RequestBody ProductoRequestDto productoRequestDto) {
		return mapper.toResponseDto(productoUseCase. crear(mapper.toDomain(productoRequestDto)));
	}
	
	@PutMapping("/{id}")
	public ProductoResponseDto actualizar(@PathVariable Long id,
			@Valid @RequestBody ProductoRequestDto productoRequestDto) {
		return mapper.toResponseDto(productoUseCase.actualizar(id.intValue(), mapper.toDomain(productoRequestDto)));
	}

	@GetMapping
	public List<ProductoResponseDto> listarTodo(){
		return productoUseCase. listar().stream().map(mapper :: toResponseDto). toList();
	}

	/**
	 * Nota: "categoriaId" se acepta para no romper al webclient (que siempre
	 * lo envia), pero todavia no se filtra por categoria porque el producto no
	 * expone esa relacion en el dominio/DTO actual.
	 */
	@GetMapping("/buscar")
	public List<ProductoResponseDto> buscar(@RequestParam(required = false) String nombre,
			@RequestParam(required = false) String marca, @RequestParam(required = false) Long categoriaId) {
		return productoUseCase.buscar(nombre, marca).stream().map(mapper::toResponseDto).toList();
	}

	/**
	 * Historico consolidado de precio (costo, margen, precio de venta, IVA e
	 * ICE aplicados y precio final) a traves del tiempo para un producto.
	 */
	@GetMapping("/{id}/historial-precios")
	public List<ProductoPrecioHistoricoResponseDto> historialPrecios(@PathVariable Long id) {
		return precioHistoricoUseCase.listarHistorial(id).stream().map(this::toHistoricoResponseDto).toList();
	}

	private ProductoPrecioHistoricoResponseDto toHistoricoResponseDto(ProductoPrecioHistorico historico) {
		ProductoPrecioHistoricoResponseDto dto = new ProductoPrecioHistoricoResponseDto();
		dto.setId(historico.getId());
		dto.setProductoId(historico.getProductoId());
		dto.setCostoPromedio(historico.getCostoPromedio());
		dto.setMargenGanancia(historico.getMargenGanancia());
		dto.setPrecioVenta(historico.getPrecioVenta());
		dto.setIvaPorcentajeAplicado(historico.getIvaPorcentajeAplicado());
		dto.setIceTipoAplicado(historico.getIceTipoAplicado());
		dto.setIceValorAplicado(historico.getIceValorAplicado());
		dto.setPrecioFinalConImpuestos(historico.getPrecioFinalConImpuestos());
		dto.setVigenteDesde(historico.getVigenteDesde());
		dto.setVigenteHasta(historico.getVigenteHasta());
		dto.setMotivo(historico.getMotivo());
		return dto;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar (@PathVariable int idGrupo){
		
		productoUseCase.eliminar(idGrupo);
		return ResponseEntity.noContent().build();
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
