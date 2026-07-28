package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

/**
 * Implementación del caso de uso de Órdenes de Compra.
 * Gestiona el ciclo de vida completo: BORRADOR → ENVIADA → RECIBIDA / ANULADA.
 */
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

    /**
     * Resuelve el negocioId: usa el del request si viene, sino toma el negocio activo.
     */
    private Integer resolverNegocioId(Integer negocioId) {
        if (negocioId != null) return negocioId;
        return negocioRepositorio.buscarActivo()
                .orElseThrow(() -> new RecursoNoEncontradoException("No hay ningún negocio activo configurado"))
                .getNegocioId();
    }

    /**
     * Crea una nueva orden de compra en estado BORRADOR con sus detalles.
     * Verifica que todos los productos existan, calcula el total y genera el código.
     */
    @Override
    @Transactional
    public OrdenCompra crearOrden(OrdenCompra orden, List<DetalleOrdenCompra> detalles, Long proveedorId) {
        // Verificar que cada productoId de los detalles existe
        for (DetalleOrdenCompra detalle : detalles) {
            productoRepositorio.buscarPorId(detalle.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con id: " + detalle.getProductoId()));
        }

        // Calcular total
        double total = detalles.stream()
                .mapToDouble(d -> d.getCantidad().doubleValue() * d.getPrecioUnitario().doubleValue())
                .sum();

        // Generar código de referencia
        TipoMovimiento tipoOc = tipoMovimientoRepositorio.buscarPorCodigo("OC")
                .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de movimiento OC no configurado"));
        Integer negocioResuelto = resolverNegocioId(orden.getNegocioId());
        Long secuencia = secuenciaUseCase.siguiente(negocioResuelto, tipoOc.getTipoMovimientoId());
        String codigoReferencia = "OC-" + String.format("%08d", secuencia);

        // Asignar campos
        orden.setCodigoReferencia(codigoReferencia);
        orden.setEstado("BORRADOR");
        orden.setTotal(total);
        orden.setCreadoEn(OffsetDateTime.now());
        orden.setNegocioId(negocioResuelto);

        // Guardar la orden con las relaciones JPA (proveedor + estado)
        OrdenCompra ordenGuardada = ordenCompraRepositorio.guardarConRelaciones(orden, proveedorId);

        // Guardar cada detalle asignando el ordenCompraId recién generado
        for (DetalleOrdenCompra detalle : detalles) {
            detalleRepositorio.guardarConOrdenCompraId(detalle, ordenGuardada.getOrdenCompraId());
        }

        // Registrar en auditoría
        logAuditoriaUseCase.registrar(
                "OrdenCompra",
                ordenGuardada.getOrdenCompraId().toString(),
                "CREAR",
                ordenGuardada);

        return ordenGuardada;
    }

    /**
     * Actualiza una orden existente en estado BORRADOR.
     * Elimina los detalles existentes y guarda los nuevos; recalcula el total.
     */
    @Override
    @Transactional
    public OrdenCompra actualizarOrden(Long id, OrdenCompra orden, List<DetalleOrdenCompra> detalles) {
        // Verificar que la orden existe
        OrdenCompra existente = ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));

        // Verificar que está en estado BORRADOR
        if (!"BORRADOR".equals(existente.getEstado())) {
            throw new ReglaNegocioException(
                    "Solo se pueden modificar órdenes en estado BORRADOR. Estado actual: "
                            + existente.getEstado());
        }

        // Eliminar detalles existentes y guardar los nuevos
        detalleRepositorio.eliminarPorOrdenCompraId(id);
        for (DetalleOrdenCompra detalle : detalles) {
            detalleRepositorio.guardarConOrdenCompraId(detalle, id);
        }

        // Recalcular total
        double total = detalles.stream()
                .mapToDouble(d -> d.getCantidad().doubleValue() * d.getPrecioUnitario().doubleValue())
                .sum();

        // Mantener datos inmutables
        orden.setOrdenCompraId(id);
        orden.setCodigoReferencia(existente.getCodigoReferencia());
        orden.setEstado("BORRADOR");
        orden.setTotal(total);
        orden.setCreadoEn(existente.getCreadoEn());

        // Guardar la orden (guardar mantiene las relaciones existentes)
        OrdenCompra actualizada = ordenCompraRepositorio.guardar(orden);

        // Registrar en auditoría
        logAuditoriaUseCase.registrar(
                "OrdenCompra",
                id.toString(),
                "ACTUALIZAR",
                actualizada);

        return actualizada;
    }

    /**
     * Envía la orden cambiando su estado a ENVIADA (solo si está en BORRADOR).
     */
    @Override
    @Transactional
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

    /**
     * Recibe la orden (solo si está en ENVIADA), genera lotes por cada detalle
     * e incrementa el stock del producto correspondiente.
     */
    @Override
    @Transactional
    public OrdenCompra recibirOrden(Long id) {
        OrdenCompra orden = ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));

        if (!"ENVIADA".equals(orden.getEstado())) {
            throw new ReglaNegocioException(
                    "La orden debe estar en estado ENVIADA para ser recibida");
        }

        // Procesar cada detalle: crear lote e incrementar stock
        List<DetalleOrdenCompra> detalles = detalleRepositorio.buscarPorOrdenCompraId(id);
        for (DetalleOrdenCompra detalle : detalles) {
            Producto producto = productoRepositorio.buscarPorId(detalle.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Producto no encontrado con id: " + detalle.getProductoId()));

            // Generar código de entrada para el lote
            TipoMovimiento tipoLote = tipoMovimientoRepositorio.buscarPorCodigo("LOTE")
                    .orElseThrow(() -> new RecursoNoEncontradoException("Tipo de movimiento LOTE no configurado"));
            Integer negocioResuelto = resolverNegocioId(orden.getNegocioId());
            Long secuencia = secuenciaUseCase.siguiente(negocioResuelto, tipoLote.getTipoMovimientoId());
            String codigoEntrada = "LOTE-" + String.format("%08d", secuencia);

            // Crear el lote de producto
            BigDecimal cantidad = detalle.getCantidad();
            BigDecimal precio = detalle.getPrecioUnitario();

            LoteProducto lote = new LoteProducto();
            lote.setCodigoEntrada(codigoEntrada);
            lote.setCantidadInicial(cantidad);
            lote.setCantidadDisponible(cantidad);
            lote.setPrecioCosto(precio);
            lote.setFechaIngreso(OffsetDateTime.now());
            lote.setFechaVencimiento(detalle.getFechaVencimiento()); // Asignar fecha de vencimiento del detalle
            lote.setNegocioId(negocioResuelto);

            // Guardar el lote asociado al producto
            LoteProducto loteGuardado = loteRepositorio.guardarConProductoId(lote, producto.getProductoId());

            // Incrementar stock del producto
            int nuevoStock = (producto.getStockActual() != null ? producto.getStockActual() : 0)
                    + detalle.getCantidad().intValue();
            producto.setStockActual(nuevoStock);
            productoRepositorio.guardar(producto);

            // Registrar movimiento de inventario tipo ENTRADA
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

        // Cambiar estado a RECIBIDA y registrar confirmación
        orden.setEstado("RECIBIDA");
        orden.setConfirmadoEn(OffsetDateTime.now());
        // TODO: Obtener UUID del usuario autenticado cuando se implemente JWT
        // Por ahora se deja null, se debe obtener del contexto de seguridad
        
        OrdenCompra guardada = ordenCompraRepositorio.guardar(orden);

        logAuditoriaUseCase.registrar("OrdenCompra", id.toString(), "RECIBIR", guardada);

        return guardada;
    }

    /**
     * Anula la orden (solo si está en BORRADOR o ENVIADA).
     */
    @Override
    @Transactional
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

    /**
     * Busca una orden de compra por su ID.
     */
    @Override
    public OrdenCompra buscarPorId(Long id) {
        return ordenCompraRepositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Orden de compra no encontrada con id: " + id));
    }

    /**
     * Lista órdenes de compra con filtros opcionales de estado y rango de fechas.
     */
    @Override
    public List<OrdenCompra> listarConFiltros(String estado, OffsetDateTime desde, OffsetDateTime hasta) {
        return ordenCompraRepositorio.buscarConFiltros(estado, desde, hasta);
    }
}
