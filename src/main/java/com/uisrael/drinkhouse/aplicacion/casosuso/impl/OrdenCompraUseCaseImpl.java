package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IMovimientoInventarioUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IOrdenCompraUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ISecuenciaCodigoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.IDetalleOrdenCompraRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.INegocioRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IOrdenCompraRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;

public class OrdenCompraUseCaseImpl implements IOrdenCompraUseCase {

    private final IOrdenCompraRepositorio ordenCompraRepositorio;
    private final IDetalleOrdenCompraRepositorio detalleRepositorio;
    private final IProductoRepositorio productoRepositorio;
    private final ILoteProductoRepositorio loteRepositorio;
    private final ISecuenciaCodigoUseCase secuenciaUseCase;
    private final ILogAuditoriaUseCase logAuditoriaUseCase;
    private final ITipoMovimientoRepositorio tipoMovimientoRepositorio;
    private final INegocioRepositorio negocioRepositorio;
    private final IMovimientoInventarioUseCase movimientoInventarioUseCase;

    public OrdenCompraUseCaseImpl(
            IOrdenCompraRepositorio ordenCompraRepositorio,
            IDetalleOrdenCompraRepositorio detalleRepositorio,
            IProductoRepositorio productoRepositorio,
            ILoteProductoRepositorio loteRepositorio,
            ISecuenciaCodigoUseCase secuenciaUseCase,
            ILogAuditoriaUseCase logAuditoriaUseCase,
            ITipoMovimientoRepositorio tipoMovimientoRepositorio,
            INegocioRepositorio negocioRepositorio,
            IMovimientoInventarioUseCase movimientoInventarioUseCase) {
        this.ordenCompraRepositorio = ordenCompraRepositorio;
        this.detalleRepositorio = detalleRepositorio;
        this.productoRepositorio = productoRepositorio;
        this.loteRepositorio = loteRepositorio;
        this.secuenciaUseCase = secuenciaUseCase;
        this.logAuditoriaUseCase = logAuditoriaUseCase;
        this.tipoMovimientoRepositorio = tipoMovimientoRepositorio;
        this.negocioRepositorio = negocioRepositorio;
        this.movimientoInventarioUseCase = movimientoInventarioUseCase;
    }

private Integer resolverNegocioId(Integer negocioId) {
        if (negocioId != null) return negocioId;
        return negocioRepositorio.buscarActivo()
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay ningún negocio activo configurado"))
                .getNegocioId();
    }

@Override
    public OrdenCompra crearOrden(OrdenCompra orden, List<DetalleOrdenCompra> detalles) {
        Long proveedorId = orden.getProveedorId();
        
        for (DetalleOrdenCompra detalle : detalles) {
            productoRepositorio.buscarPorId(detalle.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con id: " + detalle.getProductoId()));
        }

        BigDecimal total = detalles.stream()
                .map(d -> d.getCantidad().multiply(d.getPrecioUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        TipoMovimiento tipoOc = tipoMovimientoRepositorio.buscarPorCodigo("OC")
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de movimiento OC no configurado"));
        Integer negocioResuelto = resolverNegocioId(orden.getNegocioId());
        Long secuencia = secuenciaUseCase.siguiente(negocioResuelto, tipoOc.getTipoMovimientoId());
        String codigoReferencia = "OC-" + String.format("%08d", secuencia);

        orden.setCodigoReferencia(codigoReferencia);
        orden.setEstado("BORRADOR");
        orden.setTotal(total);
        orden.setCreadoEn(OffsetDateTime.now());
        orden.setNegocioId(negocioResuelto);

        OrdenCompra ordenGuardada = ordenCompraRepositorio.guardarConRelaciones(orden, proveedorId);

        for (DetalleOrdenCompra detalle : detalles) {
            detalleRepositorio.guardarConOrdenCompraId(detalle, ordenGuardada.getOrdenCompraId());
        }

        logAuditoriaUseCase.registrar(
                "OrdenCompra",
                ordenGuardada.getOrdenCompraId().toString(),
                "CREAR",
                ordenGuardada);

        return ordenGuardada;
    }

@Override
    public OrdenCompra actualizarOrden(Long id, OrdenCompra orden, List<DetalleOrdenCompra> detalles) {
        OrdenCompra existente = ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));

        if (!"BORRADOR".equals(existente.getEstado())) {
            throw new ReglaNegocioException(
                    "Solo se pueden modificar órdenes en estado BORRADOR. Estado actual: "
                            + existente.getEstado());
        }

        detalleRepositorio.eliminarPorOrdenCompraId(id);
        for (DetalleOrdenCompra detalle : detalles) {
            detalleRepositorio.guardarConOrdenCompraId(detalle, id);
        }

        BigDecimal total = detalles.stream()
                .map(d -> d.getCantidad().multiply(d.getPrecioUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        orden.setOrdenCompraId(id);
        orden.setCodigoReferencia(existente.getCodigoReferencia());
        orden.setEstado("BORRADOR");
        orden.setTotal(total);
        orden.setCreadoEn(existente.getCreadoEn());

        OrdenCompra actualizada = ordenCompraRepositorio.guardar(orden);

        logAuditoriaUseCase.registrar(
                "OrdenCompra",
                id.toString(),
                "ACTUALIZAR",
                actualizada);

        return actualizada;
    }

@Override
    public OrdenCompra enviarOrden(Long id) {
        OrdenCompra orden = ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));

        if (!"BORRADOR".equals(orden.getEstado())) {
            throw new ReglaNegocioException(
                    "La orden debe estar en estado BORRADOR para ser enviada");
        }

        orden.setEstado("ENVIADA");
        OrdenCompra guardada = ordenCompraRepositorio.guardar(orden);

        logAuditoriaUseCase.registrar("OrdenCompra", id.toString(), "ENVIAR", guardada);

        return guardada;
    }

@Override
    public OrdenCompra recibirOrden(Long id) {
        System.out.println("=== RECIBIRORDEN LLAMADO PARA ORDEN ID: " + id + " ===");
        OrdenCompra orden = ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));

        if (!"ENVIADA".equals(orden.getEstado())) {
            throw new ReglaNegocioException(
                    "La orden debe estar en estado ENVIADA para ser recibida");
        }

        List<DetalleOrdenCompra> detalles = detalleRepositorio.buscarPorOrdenCompraId(id);
        System.out.println("=== DETALLES ENCONTRADOS: " + detalles.size() + " ===");
        for (DetalleOrdenCompra detalle : detalles) {
            System.out.println("=== PROCESANDO DETALLE: ProductoID=" + detalle.getProductoId() + ", Cantidad=" + detalle.getCantidad() + " ===");
            Producto producto = productoRepositorio.buscarPorId(detalle.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con id: " + detalle.getProductoId()));

            TipoMovimiento tipoLote = tipoMovimientoRepositorio.buscarPorCodigo("LOTE")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de movimiento LOTE no configurado"));
            Integer negocioResuelto = resolverNegocioId(orden.getNegocioId());
            Long secuencia = secuenciaUseCase.siguiente(negocioResuelto, tipoLote.getTipoMovimientoId());
            String codigoEntrada = "LOTE-" + String.format("%08d", secuencia);

            BigDecimal cantidad = detalle.getCantidad();
            BigDecimal precio = detalle.getPrecioUnitario();

            LoteProducto lote = new LoteProducto();
            lote.setCodigoEntrada(codigoEntrada);
            lote.setCantidadInicial(cantidad);
            lote.setCantidadDisponible(cantidad);
            lote.setPrecioCosto(precio);
            lote.setFechaIngreso(OffsetDateTime.now());
            lote.setFechaVencimiento(detalle.getFechaVencimiento());
            lote.setNegocioId(negocioResuelto);

            LoteProducto loteGuardado = loteRepositorio.guardarConProductoId(lote, producto.getProductoId());
            System.out.println("=== LOTE CREADO: ID=" + loteGuardado.getLoteId() + ", Código=" + codigoEntrada + " ===");

            TipoMovimiento tipoEntrada = tipoMovimientoRepositorio.buscarPorCodigo("ENTRADA")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de movimiento ENTRADA no configurado"));

            MovimientoInventario movimiento = new MovimientoInventario();
            movimiento.setCantidad(cantidad);
            movimiento.setPrecioUnitario(precio);
            movimiento.setCreadoEn(OffsetDateTime.now());

            movimientoInventarioUseCase.registrar(
                    producto.getProductoId(),
                    loteGuardado.getLoteId(),
                    tipoEntrada.getTipoMovimientoId().longValue(),
                    movimiento);
        }

        orden.setEstado("RECIBIDA");
        orden.setConfirmadoEn(OffsetDateTime.now());
        
        OrdenCompra guardada = ordenCompraRepositorio.guardar(orden);

        logAuditoriaUseCase.registrar("OrdenCompra", id.toString(), "RECIBIR", guardada);

        return guardada;
    }

@Override
    public OrdenCompra anularOrden(Long id) {
        OrdenCompra orden = ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));

        String estado = orden.getEstado();
        if (!"BORRADOR".equals(estado) && !"ENVIADA".equals(estado)) {
            throw new ReglaNegocioException(
                    "Solo se pueden anular órdenes en estado BORRADOR o ENVIADA");
        }

        orden.setEstado("ANULADA");
        OrdenCompra guardada = ordenCompraRepositorio.guardar(orden);

        logAuditoriaUseCase.registrar("OrdenCompra", id.toString(), "ANULAR", guardada);

        return guardada;
    }

@Override
    public OrdenCompra buscarPorId(Long id) {
        return ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));
    }

@Override
    public List<OrdenCompra> listarConFiltros(String estado, OffsetDateTime desde, OffsetDateTime hasta) {
        return ordenCompraRepositorio.buscarConFiltros(estado, desde, hasta);
    }
}
