package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IOrdenCompraUseCase;
import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.dominio.repositorios.IDetalleOrdenCompraRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IOrdenCompraJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.presentacion.dto.request.OrdenCompraRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.DetalleOrdenCompraResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.OrdenCompraResponseDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IOrdenCompraDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ordenes-compra")
public class OrdenCompraController {

    private final IOrdenCompraUseCase ordenCompraUseCase;
    private final IOrdenCompraDtoMapper mapper;
    private final IDetalleOrdenCompraRepositorio detalleRepositorio;
    private final IOrdenCompraJpaRepositorio ordenCompraJpaRepositorio;
    private final IProductoJpaRepositorio productoJpaRepositorio;

    public OrdenCompraController(
            IOrdenCompraUseCase ordenCompraUseCase,
            IOrdenCompraDtoMapper mapper,
            IDetalleOrdenCompraRepositorio detalleRepositorio,
            IOrdenCompraJpaRepositorio ordenCompraJpaRepositorio,
            IProductoJpaRepositorio productoJpaRepositorio) {
        this.ordenCompraUseCase = ordenCompraUseCase;
        this.mapper = mapper;
        this.detalleRepositorio = detalleRepositorio;
        this.ordenCompraJpaRepositorio = ordenCompraJpaRepositorio;
        this.productoJpaRepositorio = productoJpaRepositorio;
    }

@PostMapping
	@Transactional
    public ResponseEntity<OrdenCompraResponseDto> crearOrden(
            @Valid @RequestBody OrdenCompraRequestDto requestDto) {

        OrdenCompra orden = mapper.toDomain(requestDto);
        List<DetalleOrdenCompra> detalles = requestDto.getDetalles().stream()
                .map(mapper::detalleRequestToDomain)
                .toList();

        OrdenCompra creada = ordenCompraUseCase.crearOrden(orden, detalles);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(construirResponseConDetalles(creada));
    }

@PutMapping("/{id}")
	@Transactional
    public ResponseEntity<OrdenCompraResponseDto> actualizarOrden(
            @PathVariable Long id,
            @Valid @RequestBody OrdenCompraRequestDto requestDto) {

        OrdenCompra orden = mapper.toDomain(requestDto);
        List<DetalleOrdenCompra> detalles = requestDto.getDetalles().stream()
                .map(mapper::detalleRequestToDomain)
                .toList();

        OrdenCompra actualizada = ordenCompraUseCase.actualizarOrden(id, orden, detalles);

        return ResponseEntity.ok(construirResponseConDetalles(actualizada));
    }

@PatchMapping("/{id}/enviar")
    public ResponseEntity<OrdenCompraResponseDto> enviarOrden(@PathVariable Long id) {
        OrdenCompra enviada = ordenCompraUseCase.enviarOrden(id);
        return ResponseEntity.ok(construirResponseConDetalles(enviada));
    }

@PatchMapping("/{id}/recibir")
    @Transactional
    public ResponseEntity<OrdenCompraResponseDto> recibirOrden(@PathVariable Long id) {
        OrdenCompra recibida = ordenCompraUseCase.recibirOrden(id);
        return ResponseEntity.ok(construirResponseConDetalles(recibida));
    }

@PatchMapping("/{id}/anular")
    public ResponseEntity<OrdenCompraResponseDto> anularOrden(@PathVariable Long id) {
        OrdenCompra anulada = ordenCompraUseCase.anularOrden(id);
        return ResponseEntity.ok(construirResponseConDetalles(anulada));
    }

@GetMapping("/{id}")
    public ResponseEntity<OrdenCompraResponseDto> buscarPorId(@PathVariable Long id) {
        OrdenCompra orden = ordenCompraUseCase.buscarPorId(id);
        return ResponseEntity.ok(construirResponseConDetalles(orden));
    }

@GetMapping
    public ResponseEntity<List<OrdenCompraResponseDto>> listarConFiltros(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {

        List<OrdenCompraResponseDto> lista = ordenCompraUseCase
                .listarConFiltros(estado, desde, hasta)
                .stream()
                .map(this::construirResponseConDetalles)
                .toList();

        return ResponseEntity.ok(lista);
    }

private OrdenCompraResponseDto construirResponseConDetalles(OrdenCompra orden) {
        OrdenCompraResponseDto dto = mapper.toResponseDto(orden);

        List<DetalleOrdenCompraResponseDto> detallesDto = detalleRepositorio
                .buscarPorOrdenCompraId(orden.getOrdenCompraId())
                .stream()
                .map(mapper::detalleToResponseDto)
                .toList();
        dto.setDetalles(detallesDto);

        ordenCompraJpaRepositorio.findById(orden.getOrdenCompraId()).ifPresent(entity -> {
            if (entity.getFkProveedorEntity() != null) {
                dto.setProveedorId(entity.getFkProveedorEntity().getProveedorId());
                dto.setProveedorRazonSocial(entity.getFkProveedorEntity().getRazonSocial());
            }
        });

        for (DetalleOrdenCompraResponseDto detalleDto : detallesDto) {
            if (detalleDto.getProductoId() != null) {
                productoJpaRepositorio.findById(detalleDto.getProductoId()).ifPresent(producto -> {
                    detalleDto.setProductoNombre(producto.getNombre());
                });
            }
        }

        return dto;
    }
}
