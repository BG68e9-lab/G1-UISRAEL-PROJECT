package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.EstadoOc;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoOcRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoOcEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoOcJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoOcJpaRepositorio;

public class EstadoOcRepositorioImpl implements IEstadoOcRepositorio{
	
	private final IEstadoOcJpaRepositorio jpaRepositorio;
	private final IEstadoOcJpaMapper estadoOcMapper;
	
	public EstadoOcRepositorioImpl(IEstadoOcJpaRepositorio jpaRepositorio, IEstadoOcJpaMapper estadoOcMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.estadoOcMapper = estadoOcMapper;
	}

	@Override
	public EstadoOc guardar(EstadoOc nuevoEstadoOc) {
		EstadoOcEntity entity = estadoOcMapper.toEntity(nuevoEstadoOc);
		EstadoOcEntity guardado = jpaRepositorio.save(entity);
		return estadoOcMapper.toDomain(guardado);
	}

	@Override
	public Optional<EstadoOc> buscarPorId(Integer idEstadoOc) {
		return jpaRepositorio.findById(idEstadoOc).map(estadoOcMapper::toDomain);
	}

	@Override
	public List<EstadoOc> listarTodos() {
		return jpaRepositorio.findAll().stream().map(estadoOcMapper::toDomain).toList();
	}

	@Override
	public void eliminar(Integer idEstadoOc) {
		jpaRepositorio.deleteById(idEstadoOc);
	}

}
