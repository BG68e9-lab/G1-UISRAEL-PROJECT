package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.ConsumoIaMensual;
import com.uisrael.drinkhouse.dominio.repositorios.IConsumoIaMensualRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ConsumoIaMensualEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IConsumoIaMensualJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IConsumoIaMensualJpaRepositorio;

public class ConsumoIaMensualRepositorioImpl implements IConsumoIaMensualRepositorio {

	private final IConsumoIaMensualJpaRepositorio jpaRepositorio;
	private final IConsumoIaMensualJpaMapper mapper;

	public ConsumoIaMensualRepositorioImpl(IConsumoIaMensualJpaRepositorio jpaRepositorio,
			IConsumoIaMensualJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public ConsumoIaMensual guardar(ConsumoIaMensual consumo) {
		ConsumoIaMensualEntity entidad = mapper.aEntidad(consumo);
		NegocioEntity negocioRef = new NegocioEntity();
		negocioRef.setNegocioId(consumo.getNegocioId());
		entidad.setNegocio(negocioRef);
		return mapper.aDominio(jpaRepositorio.save(entidad));
	}

	@Override
	public Optional<ConsumoIaMensual> buscarPorNegocioYPeriodo(Integer negocioId, LocalDate periodo) {
		return jpaRepositorio.findByNegocio_NegocioIdAndPeriodo(negocioId, periodo)
				.map(mapper::aDominio);
	}
}
