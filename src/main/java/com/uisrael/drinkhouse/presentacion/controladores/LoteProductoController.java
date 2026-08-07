package com.uisrael.drinkhouse.presentacion.controladores;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILoteProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.presentacion.dto.request.LoteProductoRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.LoteProductoResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.ILoteProductoDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/lotes")
public class LoteProductoController {

	private final ILoteProductoUseCase loteProductoUseCase;
	private final ILoteProductoDtoMapper mapper;

	public LoteProductoController(ILoteProductoUseCase loteProductoUseCase,
			ILoteProductoDtoMapper mapper) {
		this.loteProductoUseCase = loteProductoUseCase;
		this.mapper = mapper;
	}

@GetMapping
	public Page<LoteProductoResponseDto> listar(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size,
			@RequestParam(defaultValue = "fechaIngreso") String sortBy,
			@RequestParam(defaultValue = "desc") String sortDir) {
		Sort sort = sortDir.equalsIgnoreCase("asc")
				? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return loteProductoUseCase.listarPaginado(pageable)
				.map(mapper::aResponseDto);
	}

@PostMapping
	@Transactional
	@ResponseStatus(HttpStatus.CREATED)
	public LoteProductoResponseDto crearLote(@Valid @RequestBody LoteProductoRequestDto dto) {
		LoteProducto lote = mapper.aDominio(dto);
		LoteProducto creado = loteProductoUseCase.crearLote(lote, dto.getProductoId());
		return mapper.aResponseDto(creado);
	}

@GetMapping("/producto/{productoId}")
	public List<LoteProductoResponseDto> buscarPorProducto(@PathVariable Long productoId) {
		return loteProductoUseCase.buscarPorProducto(productoId)
				.stream()
				.map(mapper::aResponseDto)
				.toList();
	}

@GetMapping("/{id}")
	public LoteProductoResponseDto buscarPorId(@PathVariable Long id) {
		return mapper.aResponseDto(loteProductoUseCase.buscarPorId(id));
	}

@GetMapping("/proximos-vencer")
	public List<LoteProductoResponseDto> buscarProximosAVencer(
			@RequestParam(defaultValue = "7") int dias) {
		return loteProductoUseCase.buscarProximosAVencer(dias)
				.stream()
				.map(mapper::aResponseDto)
				.toList();
	}
}
