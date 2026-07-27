package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoOcRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoOcJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoOcJpaRepositorio;

public class EstadoOcRepositorioImpl implements IEstadoOcRepositorio {

	private final IEstadoOcJpaRepositorio jpaRepositorio;
	private final IEstadoOcJpaMapper mapper;

	public EstadoOcRepositorioImpl(IEstadoOcJpaRepositorio jpaRepositorio, IEstadoOcJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public EstadoOc guardar(EstadoOc estadoOc) {
		return mapper.toDomain(jpaRepositorio.save(mapper.toEntity(estadoOc)));
	}

	@Override
	public Optional<EstadoOc> buscarPorNombre(String nombre) {
		return jpaRepositorio.findByCodigo(nombre).map(mapper::toDomain);
	}

	@Override
	public Optional<EstadoOc> buscarPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<EstadoOc> listarTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public void eliminar(Integer id) {
		jpaRepositorio.deleteById(id);
	}
}
