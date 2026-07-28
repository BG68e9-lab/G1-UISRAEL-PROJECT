package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;

/**
 * Adaptador de persistencia para LoteProducto. El dominio es "plano" (IDs +
 * campos de lectura), asi que la resolucion de la relacion con Producto vive
 * aqui.
 */
public class LoteProductoRepositorioImpl implements ILoteProductoRepositorio {

	private final ILoteProductoJpaRepositorio jpaRepositorio;
	private final IProductoJpaRepositorio productoJpaRepositorio;

	public LoteProductoRepositorioImpl(ILoteProductoJpaRepositorio jpaRepositorio,
			IProductoJpaRepositorio productoJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.productoJpaRepositorio = productoJpaRepositorio;
	}

	@Override
	public LoteProducto guardar(LoteProducto loteProducto) {
		LoteProductoEntity entity;
		boolean esNuevo = loteProducto.getLoteId() == null;

		if (!esNuevo) {
			entity = jpaRepositorio.findById(loteProducto.getLoteId())
					.orElseThrow(() -> new NoSuchElementException("Lote no encontrado"));
		} else {
			entity = new LoteProductoEntity();
		}

		ProductoEntity producto = productoJpaRepositorio.findById(loteProducto.getProductoId())
				.orElseThrow(() -> new IllegalArgumentException(
						"El producto indicado no existe: " + loteProducto.getProductoId()));
		entity.setFkProductoEntity(producto);
		entity.setFkNegocioEntity(producto.getFkNegocioEntity());

		Integer cantidadInicial = loteProducto.getCantidadInicial();
		Integer cantidadDisponible = loteProducto.getCantidadDisponible() != null ? loteProducto.getCantidadDisponible()
				: cantidadInicial;
		entity.setCantidadInicial(cantidadInicial != null ? BigDecimal.valueOf(cantidadInicial) : BigDecimal.ZERO);
		entity.setCantidadDisponible(cantidadDisponible != null ? BigDecimal.valueOf(cantidadDisponible) : BigDecimal.ZERO);
		entity.setPrecioCosto(loteProducto.getPrecioCosto() != null ? loteProducto.getPrecioCosto() : BigDecimal.ZERO);
		entity.setFechaVencimiento(loteProducto.getFechaVencimiento());

		if (esNuevo) {
			// codigo_entrada esta limitado a 15 caracteres en la BD; se genera en servidor.
			String codigo = ("LT" + Long.toString(System.nanoTime(), 36)).toUpperCase();
			if (codigo.length() > 15) {
				codigo = codigo.substring(0, 15);
			}
			entity.setCodigoEntrada(codigo);

			LocalDate fechaIngreso = loteProducto.getFechaIngreso() != null ? loteProducto.getFechaIngreso() : LocalDate.now();
			entity.setFechaIngreso(fechaIngreso.atStartOfDay(ZoneOffset.UTC).toOffsetDateTime());

			entity.setUsuarioCreacion(
					loteProducto.getUsuarioCreacion() != null && !loteProducto.getUsuarioCreacion().isBlank()
							? loteProducto.getUsuarioCreacion() : "sistema");
			entity.setActivo(true);
		}

		LoteProductoEntity guardado = jpaRepositorio.save(entity);
		return toDomain(guardado);
	}

	@Override
	public Optional<LoteProducto> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(this::toDomain);
	}

	@Override
	public List<LoteProducto> listarTodos() {
		return jpaRepositorio.findAll().stream()
				.sorted(Comparator.comparing(e -> e.getFechaIngreso() != null ? e.getFechaIngreso() : OffsetDateTime.MIN))
				.map(this::toDomain)
				.toList();
	}

	@Override
	public List<LoteProducto> listarPorProducto(Long productoId) {
		return jpaRepositorio.findAll().stream()
				.filter(e -> e.getFkProductoEntity() != null && productoId.equals(e.getFkProductoEntity().getProductoId()))
				.sorted(Comparator.comparing(e -> e.getFechaIngreso() != null ? e.getFechaIngreso() : OffsetDateTime.MIN))
				.map(this::toDomain)
				.toList();
	}

	@Override
	public List<LoteProducto> listarProximosAVencer(int dias) {
		LocalDate limite = LocalDate.now().plusDays(dias);
		return jpaRepositorio.findAll().stream()
				.filter(e -> e.getFechaVencimiento() != null && !e.getFechaVencimiento().isAfter(limite))
				.sorted(Comparator.comparing(LoteProductoEntity::getFechaVencimiento))
				.map(this::toDomain)
				.toList();
	}

	@Override
	public LoteProducto actualizarCantidad(Long id, Integer nuevaCantidadDisponible) {
		LoteProductoEntity entity = jpaRepositorio.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Lote no encontrado"));

		if (nuevaCantidadDisponible == null || nuevaCantidadDisponible < 0) {
			throw new IllegalArgumentException("La cantidad disponible no puede ser negativa");
		}
		BigDecimal inicial = entity.getCantidadInicial();
		if (inicial != null && BigDecimal.valueOf(nuevaCantidadDisponible).compareTo(inicial) > 0) {
			throw new IllegalStateException("La cantidad disponible no puede superar la cantidad inicial del lote");
		}

		entity.setCantidadDisponible(BigDecimal.valueOf(nuevaCantidadDisponible));
		return toDomain(jpaRepositorio.save(entity));
	}

	@Override
	public LoteProducto activar(Long id) {
		LoteProductoEntity entity = jpaRepositorio.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Lote no encontrado"));
		entity.setActivo(true);
		return toDomain(jpaRepositorio.save(entity));
	}

	@Override
	public LoteProducto desactivar(Long id) {
		LoteProductoEntity entity = jpaRepositorio.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Lote no encontrado"));
		entity.setActivo(false);
		return toDomain(jpaRepositorio.save(entity));
	}

	@Override
	public void eliminar(Long id) {
		LoteProductoEntity entity = jpaRepositorio.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Lote no encontrado"));
		if (entity.getMovimientos() != null && !entity.getMovimientos().isEmpty()) {
			throw new IllegalStateException("No se puede eliminar el lote: tiene movimientos de inventario asociados");
		}
		jpaRepositorio.deleteById(id);
	}

	private LoteProducto toDomain(LoteProductoEntity entity) {
		LoteProducto lote = new LoteProducto();
		lote.setLoteId(entity.getLoteId());
		lote.setCodigoEntrada(entity.getCodigoEntrada());
		lote.setCantidadInicial(entity.getCantidadInicial() != null ? entity.getCantidadInicial().intValue() : null);
		lote.setCantidadDisponible(entity.getCantidadDisponible() != null ? entity.getCantidadDisponible().intValue() : null);
		lote.setPrecioCosto(entity.getPrecioCosto());
		lote.setFechaIngreso(entity.getFechaIngreso() != null ? entity.getFechaIngreso().toLocalDate() : null);
		lote.setFechaVencimiento(entity.getFechaVencimiento());
		lote.setFechaCreacion(entity.getCreadoEn() != null ? entity.getCreadoEn().toLocalDateTime() : null);
		lote.setUsuarioCreacion(entity.getUsuarioCreacion());
		lote.setActivo(entity.getActivo());

		if (entity.getFkProductoEntity() != null) {
			lote.setProductoId(entity.getFkProductoEntity().getProductoId());
			lote.setProductoNombre(entity.getFkProductoEntity().getNombre());
			lote.setProductoMarca(entity.getFkProductoEntity().getMarca());
			lote.setProductoTipo(entity.getFkProductoEntity().getTipo());
		}
		if (entity.getFkOrdenCompraEntity() != null) {
			lote.setOrdenCompraId(entity.getFkOrdenCompraEntity().getOrdenCompraId());
		}

		return lote;
	}
}
