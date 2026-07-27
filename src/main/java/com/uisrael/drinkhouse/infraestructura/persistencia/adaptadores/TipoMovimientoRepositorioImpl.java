package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITipoMovimientoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoMovimientoJpaRepositorio;

public class TipoMovimientoRepositorioImpl implements ITipoMovimientoRepositorio {

	private final ITipoMovimientoJpaRepositorio jpaRepositorio;
	private final ITipoMovimientoJpaMapper mapper;

	public TipoMovimientoRepositorioImpl(ITipoMovimientoJpaRepositorio jpaRepositorio,
			ITipoMovimientoJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public TipoMovimiento guardar(TipoMovimiento tipoMovimiento) {
		TipoMovimientoEntity entity = mapper.toEntity(tipoMovimiento);
		TipoMovimientoEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public Optional<TipoMovimiento> buscarPorId(Integer id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<TipoMovimiento> listarTodos() {
		return jpaRepositorio.findAll().stream().map(mapper::toDomain).toList();
	}

	@Override
	public boolean existePorCodigo(String codigo) {
		return jpaRepositorio.existsByCodigo(codigo);
	}

	@Override
	public Optional<TipoMovimiento> buscarPorCodigo(String codigo) {
		return jpaRepositorio.findByCodigo(codigo).map(mapper::toDomain);
	}
}
