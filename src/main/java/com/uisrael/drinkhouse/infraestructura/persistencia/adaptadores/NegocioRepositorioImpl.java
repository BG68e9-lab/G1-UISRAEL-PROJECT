package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.Negocio;
import com.uisrael.drinkhouse.dominio.repositorios.INegocioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.INegocioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.INegocioJpaRepositorio;

public class NegocioRepositorioImpl implements INegocioRepositorio {

	private final INegocioJpaRepositorio jpaRepositorio;
	private final INegocioJpaMapper negocioMappper;

	public NegocioRepositorioImpl(INegocioJpaRepositorio jpaRepositorio, INegocioJpaMapper negocioMappper) {
		this.jpaRepositorio = jpaRepositorio;
		this.negocioMappper = negocioMappper;
	}

	@Override
	public Negocio guardar(Negocio nuevoNegocio) {
		NegocioEntity entity = negocioMappper.toEntity(nuevoNegocio);
		NegocioEntity guardado = jpaRepositorio.save(entity);
		return negocioMappper.toDomain(guardado);
	}

	@Override
	public Optional<Negocio> buscarPorId(Integer id) {
		return jpaRepositorio.findById(id).map(negocioMappper::toDomain);
	}

	@Override
	public Optional<Negocio> buscarActivo() {
		return jpaRepositorio.findByActivoTrue().map(negocioMappper::toDomain);
	}

	@Override
	public boolean existePorRuc(String ruc) {
		return jpaRepositorio.existsByRuc(ruc);
	}

}
