package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IProductoJpaRepositorio;

public class ProductoRepositorioImpl implements IProductoRepositorio {

	private final IProductoJpaRepositorio jpaRepositorio;
	private final IProductoJpaMapper productoMapper;

	public ProductoRepositorioImpl(IProductoJpaRepositorio jpaRepositorio, IProductoJpaMapper productoMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.productoMapper = productoMapper;
	}

	@Override
	public Producto guardar(Producto producto) {
		ProductoEntity entity = productoMapper.toEntity(producto);
		if (entity.getActivo() == null) {
			// La columna productos.activo es NOT NULL, pero ni el dominio ni el DTO la
			// exponen todavia; por defecto un producto nuevo se crea activo.
			entity.setActivo(true);
		}
		if (entity.getIvaExento() == null) {
			entity.setIvaExento(Boolean.TRUE.equals(producto.getIvaExento()));
		}
		ProductoEntity guardado = jpaRepositorio.save(entity);
		return productoMapper.toDomain(guardado);
	}

	/**
	 * Actualiza directamente sobre la entidad ya gestionada por JPA (en vez de
	 * reusar productoMapper.toEntity(domain), que crearia una entidad nueva y
	 * anularia fkNegocioEntity/fkCategoriaEntity porque el dominio Producto no
	 * las expone). stockActual tampoco se toca aqui: lo maneja el modulo de
	 * movimientos de inventario.
	 */
	@Override
	public Producto actualizar(int id, Producto producto) {
		ProductoEntity entity = jpaRepositorio.findById((long) id)
				.orElseThrow(() -> new NoSuchElementException("Producto no encontrado: " + id));

		entity.setNombre(producto.getNombre());
		entity.setMarca(producto.getMarca());
		entity.setTipo(producto.getTipo());
		entity.setDescripcion(producto.getDescripcion());
		entity.setCostoPromedio(producto.getCostoPromedio());
		entity.setMargenGanancia(producto.getMargenGanancia());
		entity.setPrecioVenta(producto.getPrecioVenta());
		entity.setPrecioPersonalizado(producto.getPrecioPersonalizado());
		entity.setStockMinimo(producto.getStockMinimo());
		entity.setVisibleSinStock(producto.getVisibleSinStock());
		entity.setOrigenIdentificacion(producto.getOrigenIdentificacion());
		if (producto.getIvaExento() != null) {
			entity.setIvaExento(producto.getIvaExento());
		}

		ProductoEntity guardado = jpaRepositorio.save(entity);
		return productoMapper.toDomain(guardado);
	}

	@Override
	public Optional<Producto> buscarPorId(int id) {
		return jpaRepositorio.findById((long) id).map(productoMapper::toDomain);
	}

	@Override
	public List<Producto> listarTodos() {
		return jpaRepositorio.findAll().stream().map(productoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		jpaRepositorio.deleteById((long) id);
	}
}
