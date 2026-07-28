package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IMovimientoInventarioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoMovimientoJpaRepositorio;

/**
 * Adaptador de persistencia para MovimientoInventario. El dominio es "plano"
 * (IDs + campos de lectura), asi que la resolucion de relaciones con
 * Producto/Lote/TipoMovimiento vive aqui.
 */
public class MovimientoInventarioRepositorioImpl implements IMovimientoInventarioRepositorio {

	private final IMovimientoInventarioJpaRepositorio jpaRepositorio;
	private final ITipoMovimientoJpaRepositorio tipoMovimientoJpaRepositorio;
	private final IProductoJpaRepositorio productoJpaRepositorio;
	private final ILoteProductoJpaRepositorio loteProductoJpaRepositorio;

	public MovimientoInventarioRepositorioImpl(IMovimientoInventarioJpaRepositorio jpaRepositorio,
			ITipoMovimientoJpaRepositorio tipoMovimientoJpaRepositorio, IProductoJpaRepositorio productoJpaRepositorio,
			ILoteProductoJpaRepositorio loteProductoJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.tipoMovimientoJpaRepositorio = tipoMovimientoJpaRepositorio;
		this.productoJpaRepositorio = productoJpaRepositorio;
		this.loteProductoJpaRepositorio = loteProductoJpaRepositorio;
	}

	@Override
	public MovimientoInventario guardar(MovimientoInventario movimientoInventario) {
		MovimientoInventarioEntity entity = new MovimientoInventarioEntity();

		TipoMovimientoEntity tipo = tipoMovimientoJpaRepositorio.findByCodigo(movimientoInventario.getTipo())
				.orElseThrow(() -> new IllegalArgumentException(
						"Tipo de movimiento invalido: " + movimientoInventario.getTipo()));
		entity.setFkTipoMovimientoEntity(tipo);

		ProductoEntity producto = productoJpaRepositorio.findById(movimientoInventario.getProductoId())
				.orElseThrow(() -> new IllegalArgumentException(
						"El producto indicado no existe: " + movimientoInventario.getProductoId()));
		entity.setFkProductoEntity(producto);
		entity.setFkNegocioEntity(producto.getFkNegocioEntity());

		if (movimientoInventario.getLoteId() != null) {
			LoteProductoEntity lote = loteProductoJpaRepositorio.findById(movimientoInventario.getLoteId())
					.orElseThrow(() -> new IllegalArgumentException(
							"El lote indicado no existe: " + movimientoInventario.getLoteId()));
			entity.setFkLoteEntity(lote);
		}

		String codigo = (tipo.getPrefijoCodigo() != null ? tipo.getPrefijoCodigo() : "MOV") + "-"
				+ Long.toString(System.nanoTime(), 36).toUpperCase();
		entity.setCodigoMovimiento(codigo.length() > 15 ? codigo.substring(0, 15) : codigo);

		entity.setCantidad(movimientoInventario.getCantidad() != null
				? BigDecimal.valueOf(movimientoInventario.getCantidad()) : BigDecimal.ZERO);
		entity.setPrecioUnitario(movimientoInventario.getPrecioUnitario());
		entity.setDescripcion(movimientoInventario.getDescripcion());

		MovimientoInventarioEntity guardado = jpaRepositorio.save(entity);
		return toDomain(guardado);
	}

	@Override
	public Optional<MovimientoInventario> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(this::toDomain);
	}

	@Override
	public List<MovimientoInventario> listarTodo(String tipo) {
		return jpaRepositorio.findAll().stream()
				.filter(e -> tipo == null || tipo.isBlank()
						|| (e.getFkTipoMovimientoEntity() != null && tipo.equalsIgnoreCase(e.getFkTipoMovimientoEntity().getCodigo())))
				.sorted(Comparator.comparing((MovimientoInventarioEntity e) -> e.getCreadoEn() != null ? e.getCreadoEn() : OffsetDateTime.MIN).reversed())
				.map(this::toDomain)
				.toList();
	}

	@Override
	public void eliminar(Long id) {
		if (!jpaRepositorio.existsById(id)) {
			throw new NoSuchElementException("Movimiento de inventario no encontrado");
		}
		jpaRepositorio.deleteById(id);
	}

	private MovimientoInventario toDomain(MovimientoInventarioEntity entity) {
		MovimientoInventario movimiento = new MovimientoInventario();
		movimiento.setMovimientoId(entity.getMovimientoId());
		movimiento.setCodigoMovimiento(entity.getCodigoMovimiento());
		movimiento.setCantidad(entity.getCantidad() != null ? entity.getCantidad().intValue() : null);
		movimiento.setPrecioUnitario(entity.getPrecioUnitario());
		movimiento.setDescripcion(entity.getDescripcion());
		movimiento.setFechaMovimiento(entity.getCreadoEn() != null ? entity.getCreadoEn().toLocalDateTime() : null);

		if (entity.getFkTipoMovimientoEntity() != null) {
			movimiento.setTipo(entity.getFkTipoMovimientoEntity().getCodigo());
		}
		if (entity.getFkProductoEntity() != null) {
			movimiento.setProductoId(entity.getFkProductoEntity().getProductoId());
			movimiento.setProductoNombre(entity.getFkProductoEntity().getNombre());
			movimiento.setProductoMarca(entity.getFkProductoEntity().getMarca());
			movimiento.setProductoTipo(entity.getFkProductoEntity().getTipo());
		}
		if (entity.getFkLoteEntity() != null) {
			movimiento.setLoteId(entity.getFkLoteEntity().getLoteId());
			movimiento.setLoteCodigoEntrada(entity.getFkLoteEntity().getCodigoEntrada());
		}
		if (entity.getFkUsuarioEntity() != null) {
			String nombres = entity.getFkUsuarioEntity().getNombres();
			String apellidos = entity.getFkUsuarioEntity().getApellidos();
			movimiento.setUsuarioCreacion(((nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "")).trim());
		}

		return movimiento;
	}
}
