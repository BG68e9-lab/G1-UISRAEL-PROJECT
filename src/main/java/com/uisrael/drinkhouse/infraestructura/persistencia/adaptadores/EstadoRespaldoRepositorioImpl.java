package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.EstadoRespaldo;
import com.uisrael.drinkhouse.dominio.repositorios.IEstadoRespaldoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.EstadoRespaldoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IEstadoRespaldoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IEstadoRespaldoJpaRepositorio;

public class EstadoRespaldoRepositorioImpl implements IEstadoRespaldoRepositorio{

	private final IEstadoRespaldoJpaRepositorio jpaRepositorio;
	private final IEstadoRespaldoJpaMapper estadoRespaldoMapper;
	
	
	
	public EstadoRespaldoRepositorioImpl(IEstadoRespaldoJpaRepositorio jpaRepositorio,
			IEstadoRespaldoJpaMapper estadoRespaldoMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.estadoRespaldoMapper = estadoRespaldoMapper;
	}

	@Override
	public EstadoRespaldo guardar(EstadoRespaldo estadorespaldo) {
	
		EstadoRespaldoEntity entity= estadoRespaldoMapper.toEntity(estadorespaldo);
		EstadoRespaldoEntity guardado= jpaRepositorio.save(entity);
		
		return estadoRespaldoMapper.toDomain(guardado);
	}

	@Override
	public Optional<EstadoRespaldo> buscarPorId(int id) {
		return jpaRepositorio.findById(id).map(estadoRespaldoMapper::toDomain);
	}

	@Override
	public List<EstadoRespaldo> listarTodos() {

		return jpaRepositorio.findAll().stream().map(estadoRespaldoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		
		jpaRepositorio.deleteById(id);
		
	}
	
	

}
