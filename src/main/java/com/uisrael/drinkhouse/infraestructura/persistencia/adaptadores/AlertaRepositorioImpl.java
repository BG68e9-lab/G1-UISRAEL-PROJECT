package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;


import com.uisrael.drinkhouse.dominio.entidades.Alerta;
import com.uisrael.drinkhouse.dominio.repositorios.IAlertaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.AlertaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IAlertaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IAlertaJpaRepositorio;

public class AlertaRepositorioImpl implements IAlertaRepositorio {

	private final IAlertaJpaRepositorio jpaRepositorio;
	private final IAlertaJpaMapper mapper;

	public AlertaRepositorioImpl(IAlertaJpaRepositorio jpaRepositorio, IAlertaJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Alerta guardar(Alerta alerta) {
		AlertaEntity entity = mapper.toEntity(alerta);
		AlertaEntity guardado = jpaRepositorio.save(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public Optional<Alerta> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(mapper::toDomain);
	}

	@Override
	public List<Alerta> listarConFiltros(String tipoAlerta, Boolean atendida) {
		if (tipoAlerta != null && atendida != null) {
			return jpaRepositorio.findByTipoAlertaAndAtendidaOrderByCreadoEnDesc(tipoAlerta, atendida)
					.stream().map(mapper::toDomain).toList();
		} else if (tipoAlerta != null) {
			return jpaRepositorio.findByTipoAlertaOrderByCreadoEnDesc(tipoAlerta)
					.stream().map(mapper::toDomain).toList();
		} else if (atendida != null) {
			return jpaRepositorio.findByAtendidaOrderByCreadoEnDesc(atendida)
					.stream().map(mapper::toDomain).toList();
		}
		return jpaRepositorio.findAllByOrderByCreadoEnDesc()
				.stream().map(mapper::toDomain).toList();
	}

	@Override
	public long contarNoAtendidas() {
		return jpaRepositorio.countByAtendidaFalse();
	}
}
