package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.dominio.repositorios.ISecuenciaCodigoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ISecuenciaCodigoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ISecuenciaCodigoJpaRepositorio;

public class SecuenciaCodigoRepositorioImpl implements ISecuenciaCodigoRepositorio {

	private final ISecuenciaCodigoJpaRepositorio jpaRepositorio;
	private final ISecuenciaCodigoJpaMapper mapper;

	public SecuenciaCodigoRepositorioImpl(ISecuenciaCodigoJpaRepositorio jpaRepositorio,
			ISecuenciaCodigoJpaMapper mapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.mapper = mapper;
	}

	@Override
	public Optional<SecuenciaCodigo> buscarPorNegocioYTipo(Integer negocioId, Integer tipoMovimientoId) {
		return jpaRepositorio
				.findByNegocio_NegocioIdAndTipoMovimiento_TipoMovimientoId(negocioId, tipoMovimientoId)
				.map(mapper::toDomain);
	}

	@Override
	public SecuenciaCodigo guardar(SecuenciaCodigo seq) {
		Optional<SecuenciaCodigoEntity> existente = jpaRepositorio
				.findByNegocio_NegocioIdAndTipoMovimiento_TipoMovimientoId(
						seq.getNegocioId(), seq.getTipoMovimientoId());

		SecuenciaCodigoEntity entity;
		if (existente.isPresent()) {
			entity = existente.get();
			entity.setUltimoNumero(seq.getUltimoNumero());
		} else {
			entity = mapper.toEntity(seq);
		}

		SecuenciaCodigoEntity guardado = jpaRepositorio.saveAndFlush(entity);
		return mapper.toDomain(guardado);
	}

	@Override
	public List<SecuenciaCodigo> listarTodas() {
		return jpaRepositorio.findAll()
				.stream()
				.map(mapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public List<SecuenciaCodigo> listarPorNegocio(Integer negocioId) {
		return jpaRepositorio.findByNegocio_NegocioId(negocioId)
				.stream()
				.map(mapper::toDomain)
				.collect(Collectors.toList());
	}

	@Override
	public void eliminar(SecuenciaCodigo secuenciaCodigo) {
		jpaRepositorio.findByNegocio_NegocioIdAndTipoMovimiento_TipoMovimientoId(
				secuenciaCodigo.getNegocioId(),
				secuenciaCodigo.getTipoMovimientoId())
				.ifPresent(jpaRepositorio::delete);
	}
}
