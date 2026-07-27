package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.Optional;

import org.springframework.stereotype.Repository;

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
		// Cargar la entidad managed del contexto de persistencia para hacer merge
		// en lugar de crear una instancia detached con mapper.toEntity() que causa
		// NonUniqueObjectException cuando la entity original ya está en el contexto
		SecuenciaCodigoEntity managed = jpaRepositorio
				.findByNegocio_NegocioIdAndTipoMovimiento_TipoMovimientoId(
						seq.getNegocioId(), seq.getTipoMovimientoId())
				.orElseThrow(() -> new RuntimeException(
						"Secuencia no encontrada al guardar: negocio=" + seq.getNegocioId()));
		managed.setUltimoNumero(seq.getUltimoNumero());
		SecuenciaCodigoEntity guardado = jpaRepositorio.saveAndFlush(managed);
		return mapper.toDomain(guardado);
	}
}
