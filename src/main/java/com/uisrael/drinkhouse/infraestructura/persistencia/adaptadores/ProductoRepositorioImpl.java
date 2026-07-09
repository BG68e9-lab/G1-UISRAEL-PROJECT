package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
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
