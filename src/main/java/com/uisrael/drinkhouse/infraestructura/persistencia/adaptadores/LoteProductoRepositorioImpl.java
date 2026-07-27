package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILoteProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;

/**
 * Adaptador de repositorio para LoteProducto.
 * Implementa el puerto de salida ILoteProductoRepositorio usando Spring Data JPA.
 */
public class LoteProductoRepositorioImpl implements ILoteProductoRepositorio {

	private final ILoteProductoJpaRepositorio jpaRepositorio;
	private final ILoteProductoJpaMapper mapper;

	public LoteProductoRepositorioImpl(ILoteProductoJpaRepositorio jpaRepositorio,
			ILoteProductoJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	/**
	 * Guarda un lote sin asignar producto (uso interno o cuando ya viene con FK).
	 */
	@Override
	public LoteProducto guardar(LoteProducto loteProducto) {
		LoteProductoEntity entidad = mapper.aEntidad(loteProducto);
		// Preservar la relación con producto si el loteId ya existe
		if (loteProducto.getLoteId() != null) {
			jpaRepositorio.findById(loteProducto.getLoteId()).ifPresent(existente -> {
				entidad.setFkProductoEntity(existente.getFkProductoEntity());
			});
		}
		LoteProductoEntity guardado = jpaRepositorio.save(entidad);
		return mapper.aDominio(guardado);
	}

	/**
	 * Guarda el lote asociándolo al producto mediante su ID JPA.
	 */
	@Override
	public LoteProducto guardarConProductoId(LoteProducto loteProducto, Long productoId) {
		LoteProductoEntity entidad = mapper.aEntidad(loteProducto);
		// Asignar la relación con el Producto por referencia de ID
		ProductoEntity productoRef = new ProductoEntity();
		productoRef.setProductoId(productoId);
		entidad.setFkProductoEntity(productoRef);
		LoteProductoEntity guardado = jpaRepositorio.save(entidad);
		return mapper.aDominio(guardado);
	}

	/**
	 * Busca un lote por su identificador.
	 */
	@Override
	public Optional<LoteProducto> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(mapper::aDominio);
	}

	/**
	 * Retorna todos los lotes de un producto ordenados por fechaIngreso ascendente (FIFO).
	 */
	@Override
	public List<LoteProducto> buscarPorProductoOrdenadoPorFechaIngreso(Long productoId) {
		return jpaRepositorio
				.findByFkProductoEntityProductoIdOrderByFechaIngresoAsc(productoId)
				.stream().map(mapper::aDominio).toList();
	}

	/**
	 * Retorna lotes cuya fechaVencimiento sea <= limite y cantidadDisponible > 0.
	 */
	@Override
	public List<LoteProducto> buscarProximosAVencer(LocalDate limite) {
		return jpaRepositorio.findProximosAVencer(limite)
				.stream().map(mapper::aDominio).toList();
	}

	/**
	 * Lista todos los lotes sin filtros.
	 */
	@Override
	public List<LoteProducto> listarTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::aDominio).toList();
	}

	/**
	 * Lista todos los lotes con paginación.
	 */
	@Override
	public Page<LoteProducto> listarPaginado(Pageable pageable) {
		return jpaRepositorio.findAll(pageable).map(mapper::aDominio);
	}

	/**
	 * Elimina un lote por su identificador.
	 */
	@Override
	public void eliminar(Long id) {
		jpaRepositorio.deleteById(id);
	}
}
