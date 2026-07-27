package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IMovimientoInventarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IMovimientoInventarioJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoMovimientoJpaRepositorio;

/**
 * Adaptador que implementa el puerto de salida IMovimientoInventarioRepositorio
 * usando Spring Data JPA. Resuelve las referencias FK antes de persistir.
 */
public class MovimientoInventarioRepositorioImpl implements IMovimientoInventarioRepositorio {

	private final IMovimientoInventarioJpaRepositorio jpaRepositorio;
	private final IMovimientoInventarioJpaMapper mapper;
	private final IProductoJpaRepositorio productoJpaRepositorio;
	private final ILoteProductoJpaRepositorio loteJpaRepositorio;
	private final ITipoMovimientoJpaRepositorio tipoMovimientoJpaRepositorio;

	public MovimientoInventarioRepositorioImpl(
			IMovimientoInventarioJpaRepositorio jpaRepositorio,
			IMovimientoInventarioJpaMapper mapper,
			IProductoJpaRepositorio productoJpaRepositorio,
			ILoteProductoJpaRepositorio loteJpaRepositorio,
			ITipoMovimientoJpaRepositorio tipoMovimientoJpaRepositorio) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
		this.productoJpaRepositorio = productoJpaRepositorio;
		this.loteJpaRepositorio = loteJpaRepositorio;
		this.tipoMovimientoJpaRepositorio = tipoMovimientoJpaRepositorio;
	}

	/**
	 * Persiste un movimiento de inventario resolviendo las referencias FK
	 * mediante proxies JPA (getReferenceById) para evitar N consultas innecesarias.
	 */
	@Override
	public MovimientoInventario guardar(MovimientoInventario movimiento) {
		MovimientoInventarioEntity entity = mapper.toEntity(movimiento);

		// Resolver FK obligatoria: producto
		if (movimiento.getProductoId() != null) {
			ProductoEntity productoRef = productoJpaRepositorio
					.getReferenceById(movimiento.getProductoId());
			entity.setFkProductoEntity(productoRef);
		}

		// Resolver FK obligatoria: tipo de movimiento
		if (movimiento.getTipoMovimientoId() != null) {
			TipoMovimientoEntity tipoRef = tipoMovimientoJpaRepositorio
					.getReferenceById(movimiento.getTipoMovimientoId().intValue());
			entity.setFkTipoMovimientoEntity(tipoRef);
		}

		// Resolver FK opcional: lote (requerido para SALIDA, opcional para ENTRADA/AJUSTE)
		if (movimiento.getLoteId() != null) {
			LoteProductoEntity loteRef = loteJpaRepositorio
					.getReferenceById(movimiento.getLoteId());
			entity.setFkLoteEntity(loteRef);
		}

		MovimientoInventarioEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	/**
	 * Consulta movimientos de un producto aplicando filtros opcionales de tipo
	 * de movimiento y rango de fechas, ordenados por creadoEn descendente.
	 */
	@Override
	public List<MovimientoInventario> buscarPorProductoConFiltros(Long productoId, String tipo,
			OffsetDateTime desde, OffsetDateTime hasta) {
		return jpaRepositorio.buscarConFiltros(productoId, tipo, desde, hasta)
				.stream().map(mapper::toDomain).toList();
	}
}
