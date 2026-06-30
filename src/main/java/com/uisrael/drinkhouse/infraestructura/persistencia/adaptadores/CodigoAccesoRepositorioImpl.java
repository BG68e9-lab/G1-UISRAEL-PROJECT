package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CodigoAccesoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICodigoAccesoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICodigoAccesoJpaRepositorio;

public class CodigoAccesoRepositorioImpl implements ICodigoAccesoRepositorio{
	
	private final ICodigoAccesoJpaRepositorio jpaRepositorio;
	private final ICodigoAccesoJpaMapper codigoAccesoMapper;
	
	public CodigoAccesoRepositorioImpl(ICodigoAccesoJpaRepositorio jpaRepositorio,
			ICodigoAccesoJpaMapper codigoAccesoMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.codigoAccesoMapper = codigoAccesoMapper;
	}

	@Override
	public CodigoAcceso guardar(CodigoAcceso nuevoCodigoAcceso) {
		CodigoAccesoEntity entity = codigoAccesoMapper.toEntity(nuevoCodigoAcceso);
		CodigoAccesoEntity guardado = jpaRepositorio.save(entity);
		return codigoAccesoMapper.toDomain(guardado);
	}

	@Override
	public Optional<CodigoAcceso> buscarPorId(UUID idCodigoAcceso) {
		return jpaRepositorio.findById(idCodigoAcceso).map(codigoAccesoMapper::toDomain);
	}

	@Override
	public List<CodigoAcceso> listarTodos() {
		return jpaRepositorio.findAll().stream().map(codigoAccesoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(UUID idCodigoAcceso) {
		jpaRepositorio.deleteById(idCodigoAcceso);
	}

}
