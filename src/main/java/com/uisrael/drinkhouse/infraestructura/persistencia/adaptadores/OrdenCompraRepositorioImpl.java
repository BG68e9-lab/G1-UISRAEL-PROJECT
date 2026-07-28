package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.DetalleOrdenCompra;
import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;
import com.uisrael.drinkhouse.dominio.repositorios.IOrdenCompraRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.DetalleOrdenCompraEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoOcEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.OrdenCompraEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProveedorEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoOcJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.INegocioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IOrdenCompraJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProveedorJpaRepositorio;

/**
 * Adaptador de persistencia para OrdenCompra. El dominio es "plano" (IDs +
 * campos de lectura de proveedor/producto), asi que la resolucion de
 * relaciones con Proveedor/Negocio/Estado/Producto vive aqui.
 */
public class OrdenCompraRepositorioImpl implements IOrdenCompraRepositorio {

	private static final String ESTADO_BORRADOR = "BORRADOR";
	private static final String ESTADO_ENVIADA = "ENVIADA";
	private static final String ESTADO_RECIBIDA = "RECIBIDA";

	private final IOrdenCompraJpaRepositorio jpaRepositorio;
	private final IProveedorJpaRepositorio proveedorJpaRepositorio;
	private final INegocioJpaRepositorio negocioJpaRepositorio;
	private final IEstadoOcJpaRepositorio estadoOcJpaRepositorio;
	private final IProductoJpaRepositorio productoJpaRepositorio;
	private final ILoteProductoJpaRepositorio loteProductoJpaRepositorio;

	public OrdenCompraRepositorioImpl(IOrdenCompraJpaRepositorio jpaRepositorio,
			IProveedorJpaRepositorio proveedorJpaRepositorio, INegocioJpaRepositorio negocioJpaRepositorio,
			IEstadoOcJpaRepositorio estadoOcJpaRepositorio, IProductoJpaRepositorio productoJpaRepositorio,
			ILoteProductoJpaRepositorio loteProductoJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.proveedorJpaRepositorio = proveedorJpaRepositorio;
		this.negocioJpaRepositorio = negocioJpaRepositorio;
		this.estadoOcJpaRepositorio = estadoOcJpaRepositorio;
		this.productoJpaRepositorio = productoJpaRepositorio;
		this.loteProductoJpaRepositorio = loteProductoJpaRepositorio;
	}

	@Override
	public OrdenCompra guardar(OrdenCompra ordenCompra) {
		OrdenCompraEntity entity;
		boolean esNueva = ordenCompra.getOrdenCompraId() == null;

		if (!esNueva) {
			entity = jpaRepositorio.findById(ordenCompra.getOrdenCompraId())
					.orElseThrow(() -> new NoSuchElementException("Orden de compra no encontrada"));
		} else {
			entity = new OrdenCompraEntity();
		}

		if (ordenCompra.getProveedorId() != null) {
			ProveedorEntity proveedor = proveedorJpaRepositorio.findById(ordenCompra.getProveedorId().intValue())
					.orElseThrow(() -> new IllegalArgumentException(
							"El proveedor indicado no existe: " + ordenCompra.getProveedorId()));
			entity.setFkProveedorEntity(proveedor);
			if (entity.getFkNegocioEntity() == null && proveedor.getFkNegocioEntity() != null) {
				entity.setFkNegocioEntity(proveedor.getFkNegocioEntity());
			}
		} else {
			throw new IllegalArgumentException("El proveedor es obligatorio");
		}

		if (ordenCompra.getNegocioId() != null) {
			NegocioEntity negocio = negocioJpaRepositorio.findById(ordenCompra.getNegocioId())
					.orElseThrow(() -> new IllegalArgumentException(
							"El negocio indicado no existe: " + ordenCompra.getNegocioId()));
			entity.setFkNegocioEntity(negocio);
		}

		String codigoEstado = ordenCompra.getEstado() != null ? ordenCompra.getEstado() : ESTADO_BORRADOR;
		entity.setFkEstadoOcEntity(resolverEstado(codigoEstado));

		if (esNueva) {
			entity.setCodigoReferencia(generarCodigoReferencia());
			entity.setUsuarioCreacion(
					ordenCompra.getUsuarioCreacion() != null && !ordenCompra.getUsuarioCreacion().isBlank()
							? ordenCompra.getUsuarioCreacion() : "sistema");
		}
		entity.setObservaciones(ordenCompra.getObservaciones());

		// Reconstruye los detalles (cascade + orphanRemoval se encargan del resto)
		entity.getDetalles().clear();
		BigDecimal total = BigDecimal.ZERO;
		if (ordenCompra.getDetalles() != null) {
			for (DetalleOrdenCompra detalle : ordenCompra.getDetalles()) {
				DetalleOrdenCompraEntity detalleEntity = new DetalleOrdenCompraEntity();
				ProductoEntity producto = productoJpaRepositorio.findById(detalle.getProductoId())
						.orElseThrow(() -> new IllegalArgumentException(
								"El producto indicado no existe: " + detalle.getProductoId()));
				detalleEntity.setFkProductoEntity(producto);
				detalleEntity.setFkOrdenCompraEntity(entity);
				detalleEntity.setCantidad(detalle.getCantidad());
				BigDecimal precioUnitario = detalle.getPrecioUnitario() != null ? detalle.getPrecioUnitario() : BigDecimal.ZERO;
				detalleEntity.setPrecioUnitario(precioUnitario);
				BigDecimal cantidad = detalle.getCantidad() != null ? BigDecimal.valueOf(detalle.getCantidad()) : BigDecimal.ZERO;
				BigDecimal subtotal = precioUnitario.multiply(cantidad);
				detalleEntity.setSubtotal(subtotal);
				detalleEntity.setObservaciones(detalle.getObservaciones());
				entity.getDetalles().add(detalleEntity);
				total = total.add(subtotal);
			}
		}
		entity.setTotal(total);

		OrdenCompraEntity guardado = jpaRepositorio.save(entity);
		return toDomain(guardado);
	}

	@Override
	public Optional<OrdenCompra> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(this::toDomain);
	}

	@Override
	public Optional<OrdenCompra> buscarPorCodigo(String codigoReferencia) {
		return jpaRepositorio.findByCodigoReferencia(codigoReferencia).map(this::toDomain);
	}

	@Override
	public List<OrdenCompra> listarTodos(String estado) {
		List<OrdenCompraEntity> entidades = jpaRepositorio.findAll();
		return entidades.stream()
				.filter(e -> estado == null || estado.isBlank()
						|| (e.getFkEstadoOcEntity() != null && estado.equalsIgnoreCase(e.getFkEstadoOcEntity().getCodigo())))
				.sorted((a, b) -> {
					if (a.getFechaCreacion() == null || b.getFechaCreacion() == null) {
						return 0;
					}
					return b.getFechaCreacion().compareTo(a.getFechaCreacion());
				})
				.map(this::toDomain)
				.toList();
	}

	@Override
	public OrdenCompra cambiarEstado(Long id, String nuevoEstado) {
		OrdenCompraEntity entity = jpaRepositorio.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Orden de compra no encontrada"));
		entity.setFkEstadoOcEntity(resolverEstado(nuevoEstado));
		return toDomain(jpaRepositorio.save(entity));
	}

	@Override
	public OrdenCompra recibir(Long id) {
		OrdenCompraEntity entity = jpaRepositorio.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Orden de compra no encontrada"));

		String estadoActual = entity.getFkEstadoOcEntity() != null ? entity.getFkEstadoOcEntity().getCodigo() : null;
		if (!ESTADO_ENVIADA.equals(estadoActual)) {
			throw new IllegalStateException(
					"Solo se pueden recibir ordenes en estado ENVIADA (estado actual: " + estadoActual + ")");
		}

		for (DetalleOrdenCompraEntity detalle : entity.getDetalles()) {
			ProductoEntity producto = detalle.getFkProductoEntity();
			if (producto == null) {
				continue;
			}

			LoteProductoEntity lote = new LoteProductoEntity();
			lote.setFkProductoEntity(producto);
			lote.setFkNegocioEntity(producto.getFkNegocioEntity());
			lote.setFkOrdenCompraEntity(entity);
			String codigo = ("LT" + Long.toString(System.nanoTime(), 36)).toUpperCase();
			lote.setCodigoEntrada(codigo.length() > 15 ? codigo.substring(0, 15) : codigo);
			BigDecimal cantidad = detalle.getCantidad() != null ? BigDecimal.valueOf(detalle.getCantidad()) : BigDecimal.ZERO;
			lote.setCantidadInicial(cantidad);
			lote.setCantidadDisponible(cantidad);
			lote.setPrecioCosto(detalle.getPrecioUnitario());
			lote.setFechaIngreso(OffsetDateTime.now(ZoneOffset.UTC));
			lote.setUsuarioCreacion(entity.getUsuarioCreacion() != null ? entity.getUsuarioCreacion() : "sistema");
			lote.setActivo(true);
			loteProductoJpaRepositorio.save(lote);

			int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;
			int cantidadDetalle = detalle.getCantidad() != null ? detalle.getCantidad() : 0;
			producto.setStockActual(stockActual + cantidadDetalle);
			productoJpaRepositorio.save(producto);
		}

		entity.setFkEstadoOcEntity(resolverEstado(ESTADO_RECIBIDA));
		return toDomain(jpaRepositorio.save(entity));
	}

	@Override
	public void eliminar(Long id) {
		if (!jpaRepositorio.existsById(id)) {
			throw new NoSuchElementException("Orden de compra no encontrada");
		}
		jpaRepositorio.deleteById(id);
	}

	private EstadoOcEntity resolverEstado(String codigo) {
		return estadoOcJpaRepositorio.findByCodigo(codigo)
				.orElseThrow(() -> new IllegalArgumentException(
						"Estado de orden de compra invalido: '" + codigo
								+ "'. Codigos existentes en la tabla estados_oc: " + codigosEstadoDisponibles()
								+ ". Si la lista esta vacia, hay que sembrar la tabla catalogo (ver scripts/seed_catalogos.sql)."));
	}

	private String codigosEstadoDisponibles() {
		List<String> codigos = estadoOcJpaRepositorio.findAll().stream()
				.map(EstadoOcEntity::getCodigo)
				.toList();
		return codigos.isEmpty() ? "(ninguno, la tabla esta vacia)" : codigos.toString();
	}

	private String generarCodigoReferencia() {
		String fecha = LocalDate.now().toString().replace("-", "");
		String sufijo = Long.toString(System.nanoTime(), 36).toUpperCase();
		String codigo = "OC-" + fecha + "-" + sufijo;
		return codigo.length() > 50 ? codigo.substring(0, 50) : codigo;
	}

	private OrdenCompra toDomain(OrdenCompraEntity entity) {
		OrdenCompra orden = new OrdenCompra();
		orden.setOrdenCompraId(entity.getOrdenCompraId());
		orden.setCodigoReferencia(entity.getCodigoReferencia());
		orden.setTotal(entity.getTotal());
		orden.setFechaCreacion(entity.getFechaCreacion());
		orden.setUsuarioCreacion(entity.getUsuarioCreacion());
		orden.setObservaciones(entity.getObservaciones());
		orden.setVersion(entity.getVersion());

		if (entity.getFkProveedorEntity() != null) {
			orden.setProveedorId(entity.getFkProveedorEntity().getProveedorId().longValue());
			orden.setProveedorRazonSocial(entity.getFkProveedorEntity().getRazonSocial());
		}
		if (entity.getFkNegocioEntity() != null) {
			orden.setNegocioId(entity.getFkNegocioEntity().getNegocioId());
		}
		if (entity.getFkEstadoOcEntity() != null) {
			orden.setEstado(entity.getFkEstadoOcEntity().getCodigo());
		}

		List<DetalleOrdenCompra> detalles = new ArrayList<>();
		if (entity.getDetalles() != null) {
			for (DetalleOrdenCompraEntity detalleEntity : entity.getDetalles()) {
				DetalleOrdenCompra detalle = new DetalleOrdenCompra();
				detalle.setDetalleOrdenCompraId(detalleEntity.getDetalleOrdenCompraId());
				detalle.setOrdenCompraId(entity.getOrdenCompraId());
				detalle.setCantidad(detalleEntity.getCantidad());
				detalle.setPrecioUnitario(detalleEntity.getPrecioUnitario());
				detalle.setSubtotal(detalleEntity.getSubtotal());
				detalle.setObservaciones(detalleEntity.getObservaciones());
				if (detalleEntity.getFkProductoEntity() != null) {
					detalle.setProductoId(detalleEntity.getFkProductoEntity().getProductoId());
					detalle.setProductoNombre(detalleEntity.getFkProductoEntity().getNombre());
					detalle.setProductoMarca(detalleEntity.getFkProductoEntity().getMarca());
					detalle.setProductoTipo(detalleEntity.getFkProductoEntity().getTipo());
				}
				detalles.add(detalle);
			}
		}
		orden.setDetalles(detalles);

		return orden;
	}
}
