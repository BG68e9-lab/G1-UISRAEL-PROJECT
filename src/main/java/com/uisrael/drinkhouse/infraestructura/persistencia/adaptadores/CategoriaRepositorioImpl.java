package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Categoria;
import com.uisrael.drinkhouse.dominio.repositorios.ICategoriaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CategoriaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICategoriaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICategoriaJpaRepositorio;

public class CategoriaRepositorioImpl implements ICategoriaRepositorio{
	
	private final ICategoriaJpaRepositorio jpaRepositorio;
	private final ICategoriaJpaMapper categoriaMapper;

	public CategoriaRepositorioImpl(ICategoriaJpaRepositorio jpaRepositorio, ICategoriaJpaMapper categoriaMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.categoriaMapper = categoriaMapper;
	}

	@Override
	public Categoria guardar(Categoria nuevoCategoria) {
		CategoriaEntity entity = categoriaMapper.toEntity(nuevoCategoria);
		CategoriaEntity guardado = jpaRepositorio.save(entity);
		return categoriaMapper.toDomain(guardado);
	}

	@Override
	public Optional<Categoria> buscarPorId(Integer idCategoria) {
		return jpaRepositorio.findById(idCategoria).map(categoriaMapper::toDomain);
	}

	@Override
	public List<Categoria> listarTodos() {
		return jpaRepositorio.findAll().stream().map(categoriaMapper::toDomain).toList();
	}

	@Override
	public void eliminar(Integer idCategoria) {
		jpaRepositorio.deleteById(idCategoria);
	}

}
