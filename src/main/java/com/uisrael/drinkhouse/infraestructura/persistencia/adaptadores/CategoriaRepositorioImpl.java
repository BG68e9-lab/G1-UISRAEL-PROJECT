package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;
import com.uisrael.drinkhouse.dominio.repositorios.ICategoriaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICategoriaJpaRepositorio;

public class CategoriaRepositorioImpl implements ICategoriaRepositorio {

	private final ICategoriaJpaRepositorio jpaRepositorio;
	private final ICategoriaJpaMapper categoriaMapper;

	public CategoriaRepositorioImpl(ICategoriaJpaRepositorio jpaRepositorio, ICategoriaJpaMapper categoriaMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.categoriaMapper = categoriaMapper;
	}

	@Override
	public Categoria guardar(Categoria categoria) {
		CategoriaEntity entity = categoriaMapper.toEntity(categoria);
		CategoriaEntity guardado = jpaRepositorio.save(entity);
		return categoriaMapper.toDomain(guardado);
	}

	@Override
	public Optional<Categoria> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(categoriaMapper::toDomain);
	}

	@Override
	public List<Categoria> listarTodas() {
		return jpaRepositorio.findAll().stream().map(categoriaMapper::toDomain).toList();
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
	public boolean tieneProductosAsociados(Long id) {
		return jpaRepositorio.tieneProductos(id);
	}

}
