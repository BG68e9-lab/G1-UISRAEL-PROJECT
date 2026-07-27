package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.CodigoAcceso;
import com.uisrael.drinkhouse.dominio.repositorios.ICodigoAccesoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.CodigoAccesoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ICodigoAccesoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ICodigoAccesoJpaRepositorio;

public class CodigoAccesoRepositorioImpl implements ICodigoAccesoRepositorio {

	private final ICodigoAccesoJpaRepositorio jpaRepositorio;
	private final ICodigoAccesoJpaMapper codigoAccesoMapper;

	public CodigoAccesoRepositorioImpl(ICodigoAccesoJpaRepositorio jpaRepositorio,
			ICodigoAccesoJpaMapper codigoAccesoMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.codigoAccesoMapper = codigoAccesoMapper;
	}

	@Override
	public CodigoAcceso guardar(CodigoAcceso codigoAcceso) {
		CodigoAccesoEntity entity = codigoAccesoMapper.toEntity(codigoAcceso);
		CodigoAccesoEntity guardado = jpaRepositorio.save(entity);
		return codigoAccesoMapper.toDomain(guardado);
	}

	@Override
	public Optional<CodigoAcceso> buscarPorHash(String codigoHash) {
		return jpaRepositorio.findByCodigoHash(codigoHash).map(codigoAccesoMapper::toDomain);
	}
}
