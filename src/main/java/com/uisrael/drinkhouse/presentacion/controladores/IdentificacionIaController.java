package com.uisrael.drinkhouse.presentacion.controladores;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IIdentificacionIaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IOrdenCompraUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProveedorUseCase;
import com.uisrael.drinkhouse.aplicacion.servicios.FacturaIAService;
import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.entidades.Proveedor;
import com.uisrael.drinkhouse.infraestructura.servicios.ClaudeVisionService;
import com.uisrael.drinkhouse.infraestructura.servicios.ValidacionProductoExternoService;
import com.uisrael.drinkhouse.presentacion.dto.request.IdentificacionIaRequestDto;
import com.uisrael.drinkhouse.presentacion.dto.response.IdentificacionIaResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.OrdenCompraAutoResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.OrdenCompraResponseDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoBotellaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoFacturaDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ResultadoProductoDto;
import com.uisrael.drinkhouse.presentacion.dto.response.ValidacionProductoExternoDto;
import com.uisrael.drinkhouse.presentacion.mapeadores.IIdentificacionIaDtoMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/ia")
public class IdentificacionIaController {

    private final IIdentificacionIaUseCase identificacionUseCase;
    private final IIdentificacionIaDtoMapper dtoMapper;
    private final ClaudeVisionService claudeVisionService;
    private final ValidacionProductoExternoService validacionExternaService;
    private final IProveedorUseCase proveedorUseCase;
    private final IProductoUseCase productoUseCase;
    private final FacturaIAService facturaIAService;
    private final IOrdenCompraUseCase ordenCompraUseCase;

    public IdentificacionIaController(
            IIdentificacionIaUseCase identificacionUseCase,
            IIdentificacionIaDtoMapper dtoMapper,
            ClaudeVisionService claudeVisionService,
            ValidacionProductoExternoService validacionExternaService,
            IProveedorUseCase proveedorUseCase,
            IProductoUseCase productoUseCase,
            FacturaIAService facturaIAService,
            IOrdenCompraUseCase ordenCompraUseCase) {
        this.identificacionUseCase = identificacionUseCase;
        this.dtoMapper = dtoMapper;
        this.claudeVisionService = claudeVisionService;
        this.validacionExternaService = validacionExternaService;
        this.proveedorUseCase = proveedorUseCase;
        this.productoUseCase = productoUseCase;
        this.facturaIAService = facturaIAService;
        this.ordenCompraUseCase = ordenCompraUseCase;
    }

@PostMapping("/identificar")
	@Transactional
    public ResponseEntity<IdentificacionIaResponseDto> identificarProducto(
            @Valid @RequestBody IdentificacionIaRequestDto solicitud) {

        IdentificacionIa resultado = identificacionUseCase.identificarProducto(
                solicitud.getImagenBase64(),
                solicitud.getFormatoImagen(),
                solicitud.getProductoId(),
                solicitud.getNegocioId(),
                solicitud.getTipoIdentificacion());

        IdentificacionIaResponseDto respuesta = dtoMapper.aResponseDto(resultado);

        enricherConResultadoClaude(respuesta, solicitud);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

@GetMapping("/historial")
    public ResponseEntity<List<IdentificacionIaResponseDto>> consultarHistorial(
            @RequestParam(required = false) Long productoId,
            @RequestParam(required = false) OffsetDateTime desde,
            @RequestParam(required = false) OffsetDateTime hasta) {

        List<IdentificacionIa> historial = identificacionUseCase.consultarHistorial(
                productoId, desde, hasta);

        List<IdentificacionIaResponseDto> respuesta = dtoMapper.aListaResponseDto(historial);
        return ResponseEntity.ok(respuesta);
    }

private void enricherConResultadoClaude(
            IdentificacionIaResponseDto respuesta,
            IdentificacionIaRequestDto solicitud) {

        respuesta.setTipoIdentificacion(solicitud.getTipoIdentificacion().toUpperCase());

        if ("PRODUCTO".equalsIgnoreCase(solicitud.getTipoIdentificacion())) {
            ResultadoProductoDto resultadoProducto = claudeVisionService.identificarProductoGenerico(
                    solicitud.getImagenBase64(), solicitud.getFormatoImagen()).getResultado();
            respuesta.setResultadoProducto(resultadoProducto);
            respuesta.setReconocido(resultadoProducto.getReconocido());

            if (Boolean.TRUE.equals(resultadoProducto.getReconocido())) {
                ValidacionProductoExternoDto validacion = validacionExternaService.validarProducto(
                        resultadoProducto.getNombre(),
                        resultadoProducto.getMarca(),
                        resultadoProducto.getCategoriaSugerida(),
                        resultadoProducto.getInformacionAdicional()
                );
                respuesta.setValidacionExterna(validacion);
            }

        } else if ("BOTELLA".equalsIgnoreCase(solicitud.getTipoIdentificacion())) {
            ResultadoBotellaDto resultadoBotella = claudeVisionService.identificarBotella(
                    solicitud.getImagenBase64(), solicitud.getFormatoImagen()).getResultado();
            respuesta.setResultadoBotella(resultadoBotella);
            respuesta.setReconocido(resultadoBotella.getReconocido());

            if (Boolean.TRUE.equals(resultadoBotella.getReconocido())) {
                ValidacionProductoExternoDto validacion = validacionExternaService.validarProducto(
                        resultadoBotella.getNombre(),
                        resultadoBotella.getMarca(),
                        resultadoBotella.getTipo(),
                        resultadoBotella.getPresentacion()
                );
                respuesta.setValidacionExterna(validacion);
            }

        } else if ("FACTURA".equalsIgnoreCase(solicitud.getTipoIdentificacion())) {
            ResultadoFacturaDto resultadoFactura = claudeVisionService.extraerFactura(
                    solicitud.getImagenBase64(), solicitud.getFormatoImagen()).getResultado();
            
            if (resultadoFactura.getProductos() != null && !resultadoFactura.getProductos().isEmpty()) {
                validarProductosExistentes(resultadoFactura);
            }
            
            respuesta.setResultadoFactura(resultadoFactura);
            respuesta.setReconocido(resultadoFactura.getNumeroFactura() != null);
            
            if (resultadoFactura.getRucProveedor() != null && resultadoFactura.getRazonSocialProveedor() != null) {
                try {
                    Proveedor proveedor = crearProveedorSiNoExiste(resultadoFactura, solicitud.getNegocioId());
                    respuesta.setNombreSugerido(proveedor.getRazonSocial() + " (ID: " + proveedor.getProveedorId() + ")");
                } catch (Exception e) {
                    System.err.println("Error al crear proveedor automáticamente: " + e.getMessage());
                }
            }
        }
    }

private void validarProductosExistentes(ResultadoFacturaDto resultadoFactura) {
        List<Producto> productosExistentes = productoUseCase.listarProductos();
        
        for (ResultadoFacturaDto.ProductoFacturaDto productoFactura : resultadoFactura.getProductos()) {
            Producto productoEncontrado = buscarProductoPorNombre(
                productoFactura.getNombre(), 
                productosExistentes
            );
            
            if (productoEncontrado != null) {
                productoFactura.setProductoId(productoEncontrado.getProductoId());
                productoFactura.setProductoExiste(true);
            } else {
                productoFactura.setProductoId(null);
                productoFactura.setProductoExiste(false);
            }
        }
    }

private Proveedor crearProveedorSiNoExiste(ResultadoFacturaDto resultadoFactura, Integer negocioId) {
        FacturaIAService.ProveedorIADto proveedorIA = new FacturaIAService.ProveedorIADto();
        proveedorIA.setRuc(resultadoFactura.getRucProveedor());
        proveedorIA.setRazonSocial(resultadoFactura.getRazonSocialProveedor());
        
        String email = "proveedor-" + resultadoFactura.getRucProveedor() + "@temporal.ec";
        proveedorIA.setEmail(email);
        
        return facturaIAService.procesarProveedorDeFactura(proveedorIA, negocioId);
    }

@PostMapping("/crear-orden-desde-factura")
	@Transactional
    public ResponseEntity<OrdenCompraAutoResponseDto> crearOrdenDesdeFactura(
            @Valid @RequestBody IdentificacionIaRequestDto solicitud) {
        
        if (!"FACTURA".equalsIgnoreCase(solicitud.getTipoIdentificacion())) {
            throw new IllegalArgumentException(
                "Este endpoint solo acepta tipoIdentificacion='FACTURA'. Recibido: " 
                + solicitud.getTipoIdentificacion()
            );
        }
        
        ResultadoFacturaDto facturaExtraida = claudeVisionService.extraerFactura(
                solicitud.getImagenBase64(), 
                solicitud.getFormatoImagen()
        ).getResultado();
        
        if (facturaExtraida.getRucProveedor() == null || facturaExtraida.getRazonSocialProveedor() == null) {
            throw new IllegalArgumentException(
                "No se pudo extraer datos del proveedor de la factura (RUC o razón social faltantes)"
            );
        }
        
        Proveedor proveedor = crearProveedorSiNoExiste(facturaExtraida, solicitud.getNegocioId());
        boolean proveedorNuevo = proveedor.getEmail().contains("@temporal.ec");
        
        List<DetalleOrdenCompra> detalles = procesarProductosDeFactura(
            facturaExtraida.getProductos(), 
            solicitud.getNegocioId().longValue()
        );
        
        OrdenCompra nuevaOrden = new OrdenCompra();
        nuevaOrden.setNegocioId(solicitud.getNegocioId());
        nuevaOrden.setProveedorId(proveedor.getProveedorId());
        
        OrdenCompra ordenCreada = ordenCompraUseCase.crearOrden(nuevaOrden, detalles);
        
        IdentificacionIa identificacion = identificacionUseCase.identificarProducto(
                solicitud.getImagenBase64(),
                solicitud.getFormatoImagen(),
                null,
                solicitud.getNegocioId(),
                "FACTURA");
        
        OrdenCompraAutoResponseDto respuesta = OrdenCompraAutoResponseDto.builder()
            .identificacionIaId(identificacion.getIdentificacionId())
            .proveedor(OrdenCompraAutoResponseDto.ProveedorInfoDto.builder()
                .proveedorId(proveedor.getProveedorId())
                .ruc(proveedor.getRuc())
                .razonSocial(proveedor.getRazonSocial())
                .email(proveedor.getEmail())
                .emailTemporal(proveedorNuevo)
                .build())
            .proveedorNuevo(proveedorNuevo)
            .ordenCompra(mapearOrdenCompraResponse(ordenCreada, proveedor))
            .facturaExtraida(facturaExtraida)
            .mensaje(construirMensajeInformativo(proveedorNuevo, facturaExtraida, detalles.size()))
            .build();
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

private List<DetalleOrdenCompra> procesarProductosDeFactura(
            List<ResultadoFacturaDto.ProductoFacturaDto> productosFactura,
            Long negocioId) {
        
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        
        if (productosFactura == null || productosFactura.isEmpty()) {
            return detalles;
        }
        
        List<Producto> productosExistentes = productoUseCase.listarProductos();
        
        for (ResultadoFacturaDto.ProductoFacturaDto productoFactura : productosFactura) {
            try {
                Producto productoEncontrado = buscarProductoPorNombre(
                    productoFactura.getNombre(), 
                    productosExistentes
                );
                
                Long productoId;
                
                if (productoEncontrado != null) {
                    productoId = productoEncontrado.getProductoId();
                } else {
                    productoId = crearProductoAutomatico(productoFactura, negocioId);
                }
                
                DetalleOrdenCompra detalle = new DetalleOrdenCompra();
                detalle.setProductoId(productoId);
                detalle.setCantidad(
                    productoFactura.getCantidad() != null 
                        ? BigDecimal.valueOf(productoFactura.getCantidad()) 
                        : BigDecimal.ONE
                );
                detalle.setPrecioUnitario(
                    productoFactura.getPrecioUnitario() != null 
                        ? BigDecimal.valueOf(productoFactura.getPrecioUnitario()) 
                        : BigDecimal.ZERO
                );
                
                detalles.add(detalle);
                
            } catch (Exception e) {
                System.err.println("Error procesando producto de factura: " + productoFactura.getNombre() + " - " + e.getMessage());
            }
        }
        
        return detalles;
    }

private Producto buscarProductoPorNombre(String nombreBuscado, List<Producto> productosExistentes) {
        if (nombreBuscado == null || nombreBuscado.isBlank()) {
            return null;
        }
        
        String nombreNormalizado = normalizarTexto(nombreBuscado);
        
        for (Producto producto : productosExistentes) {
            String nombreProductoNormalizado = normalizarTexto(producto.getNombre());
            
            if (nombreNormalizado.equals(nombreProductoNormalizado)) {
                return producto;
            }
            
            if (nombreNormalizado.contains(nombreProductoNormalizado) || 
                nombreProductoNormalizado.contains(nombreNormalizado)) {
                return producto;
            }
        }
        
        return null;
    }

private String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
            .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
            .toLowerCase()
            .replaceAll("\\s+", " ")
            .trim();
    }

private Long crearProductoAutomatico(ResultadoFacturaDto.ProductoFacturaDto productoFactura, Long negocioId) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setNegocioId(negocioId.intValue());
        nuevoProducto.setNombre(productoFactura.getNombre());
        nuevoProducto.setMarca(productoFactura.getMarca() != null ? productoFactura.getMarca() : "Genérico");
        
        Long tipoProductoId = determinarTipoProducto(productoFactura);
        if (tipoProductoId != null) {
            nuevoProducto.setTipoProductoId(tipoProductoId);
        }
        
        BigDecimal costoPromedio = productoFactura.getPrecioUnitario() != null 
            ? BigDecimal.valueOf(productoFactura.getPrecioUnitario())
            : BigDecimal.ZERO;
        
        nuevoProducto.setCostoPromedio(costoPromedio);
        nuevoProducto.setPrecioVenta(costoPromedio.multiply(BigDecimal.valueOf(1.30)));
        
        nuevoProducto.setActivo(true);
        nuevoProducto.setStockActual(0);
        nuevoProducto.setStockMinimo(5);
        nuevoProducto.setPermiteStockNegativo(false);
        nuevoProducto.setVisibleSinStock(true);
        nuevoProducto.setOrigenIdentificacion("IA_FACTURA");
        
        Producto productoCreado = productoUseCase.crearProducto(nuevoProducto);
        return productoCreado.getProductoId();
    }

private Long determinarTipoProducto(ResultadoFacturaDto.ProductoFacturaDto productoFactura) {
        return null;
    }

private OrdenCompraResponseDto mapearOrdenCompraResponse(OrdenCompra orden, Proveedor proveedor) {
        OrdenCompraResponseDto response = new OrdenCompraResponseDto();
        response.setOrdenCompraId(orden.getOrdenCompraId());
        response.setCodigoReferencia(orden.getCodigoReferencia());
        response.setEstado(orden.getEstado());
        response.setTotal(orden.getTotal());
        response.setCreadoEn(orden.getCreadoEn());
        response.setProveedorId(proveedor.getProveedorId());
        response.setProveedorRazonSocial(proveedor.getRazonSocial());
        response.setDetalles(new ArrayList<>());
        return response;
    }

private String construirMensajeInformativo(boolean proveedorNuevo, ResultadoFacturaDto factura, int productosCreados) {
        StringBuilder mensaje = new StringBuilder();
        
        if (proveedorNuevo) {
            mensaje.append("✅ Proveedor creado automáticamente. ");
        } else {
            mensaje.append("✅ Proveedor encontrado en sistema. ");
        }
        
        mensaje.append(String.format("✅ Orden de compra creada con %d productos procesados automáticamente.", productosCreados));
        
        if (factura.getProductos() != null && !factura.getProductos().isEmpty()) {
            int detectados = factura.getProductos().size();
            if (detectados != productosCreados) {
                mensaje.append(String.format(" (%d detectados, %d procesados correctamente)", 
                    detectados, productosCreados));
            }
        }
        
        return mensaje.toString();
    }
}
