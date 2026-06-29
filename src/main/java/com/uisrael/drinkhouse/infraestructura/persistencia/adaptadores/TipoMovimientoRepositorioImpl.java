package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TipoMovimiento;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoMovimientoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoMovimientoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITipoMovimientoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoMovimientoJpaRepositorio;

public class TipoMovimientoRepositorioImpl implements ITipoMovimientoRepositorio {

	private final ITipoMovimientoJpaRepositorio jpaRepositorio;
	private final ITipoMovimientoJpaMapper tipoMovimientoMapper;

	public TipoMovimientoRepositorioImpl(ITipoMovimientoJpaRepositorio jpaRepositorio,
			ITipoMovimientoJpaMapper tipoMovimientMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.tipoMovimientoMapper = tipoMovimientMapper;
	}

	@Override
	public TipoMovimiento guardar(TipoMovimiento tipomovimiento) {
		TipoMovimientoEntity entity = tipoMovimientoMapper.toEntity(tipomovimiento);
		TipoMovimientoEntity guardado = jpaRepositorio.save(entity);
		return tipoMovimientoMapper.toDomain(guardado);
	}

	@Override
	public Optional<TipoMovimiento> buscarPorId(int id) {

		return jpaRepositorio.findById(id).map(tipoMovimientoMapper::toDomain);
	}

	@Override
	public List<TipoMovimiento> listarTodos() {

		return jpaRepositorio.findAll().stream().map(tipoMovimientoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		jpaRepositorio.deleteById(id);

	}

}
