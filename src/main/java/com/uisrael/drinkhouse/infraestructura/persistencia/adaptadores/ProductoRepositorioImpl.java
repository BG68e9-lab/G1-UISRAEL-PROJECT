package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.especificaciones.ProductoSpecification;
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
	public Optional<Producto> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(productoMapper::toDomain);
	}

	@Override
	public List<Producto> listarTodos() {
		return jpaRepositorio.findAll().stream().map(productoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(Long id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public boolean existePorNombre(String nombre) {
		return jpaRepositorio.existsByNombre(nombre);
	}

	@Override
	public List<Producto> buscarConFiltros(String nombre, String marca, String tipo, Long categoriaId) {
		return jpaRepositorio.findAll(ProductoSpecification.conFiltros(nombre, marca, tipo, categoriaId))
				.stream().map(productoMapper::toDomain).toList();
	}
}
