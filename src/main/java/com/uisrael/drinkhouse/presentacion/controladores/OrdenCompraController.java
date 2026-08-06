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

/**
 * Controlador REST para el módulo de Órdenes de Compra.
 * Base URL: /api/v1/ordenes-compra
 */
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

    /**
     * POST /api/v1/ordenes-compra
     * Crea una nueva orden de compra en estado BORRADOR.
     *
     * @param requestDto datos de la orden y sus detalles
     * @return la orden creada, HTTP 201
     */
    @PostMapping
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

    /**
     * PUT /api/v1/ordenes-compra/{id}
     * Actualiza una orden en estado BORRADOR.
     *
     * @param id         ID de la orden a actualizar
     * @param requestDto nuevos datos de la orden
     * @return la orden actualizada, HTTP 200
     */
    @PutMapping("/{id}")
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

    /**
     * PATCH /api/v1/ordenes-compra/{id}/enviar
     * Envía la orden cambiando su estado a ENVIADA.
     *
     * @param id ID de la orden
     * @return la orden con estado ENVIADA, HTTP 200
     */
    @PatchMapping("/{id}/enviar")
    public ResponseEntity<OrdenCompraResponseDto> enviarOrden(@PathVariable Long id) {
        OrdenCompra enviada = ordenCompraUseCase.enviarOrden(id);
        return ResponseEntity.ok(construirResponseConDetalles(enviada));
    }

    /**
     * PATCH /api/v1/ordenes-compra/{id}/recibir
     * Recibe la orden generando lotes e incrementando stock.
     *
     * @param id ID de la orden
     * @return la orden con estado RECIBIDA, HTTP 200
     */
    @PatchMapping("/{id}/recibir")
    public ResponseEntity<OrdenCompraResponseDto> recibirOrden(@PathVariable Long id) {
        OrdenCompra recibida = ordenCompraUseCase.recibirOrden(id);
        return ResponseEntity.ok(construirResponseConDetalles(recibida));
    }

    /**
     * PATCH /api/v1/ordenes-compra/{id}/anular
     * Anula la orden (solo en estado BORRADOR o ENVIADA).
     *
     * @param id ID de la orden
     * @return la orden con estado ANULADA, HTTP 200
     */
    @PatchMapping("/{id}/anular")
    public ResponseEntity<OrdenCompraResponseDto> anularOrden(@PathVariable Long id) {
        OrdenCompra anulada = ordenCompraUseCase.anularOrden(id);
        return ResponseEntity.ok(construirResponseConDetalles(anulada));
    }

    /**
     * GET /api/v1/ordenes-compra/{id}
     * Busca una orden por su ID.
     *
     * @param id ID de la orden
     * @return la orden encontrada, HTTP 200
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrdenCompraResponseDto> buscarPorId(@PathVariable Long id) {
        OrdenCompra orden = ordenCompraUseCase.buscarPorId(id);
        return ResponseEntity.ok(construirResponseConDetalles(orden));
    }

    /**
     * GET /api/v1/ordenes-compra
     * Lista órdenes con filtros opcionales de estado y rango de fechas.
     *
     * @param estado código del estado (opcional)
     * @param desde  fecha de inicio del rango ISO-8601 (opcional)
     * @param hasta  fecha de fin del rango ISO-8601 (opcional)
     * @return lista de órdenes, HTTP 200
     */
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

    /**
     * Construye el DTO de respuesta incluyendo los detalles de la orden.
     */
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
