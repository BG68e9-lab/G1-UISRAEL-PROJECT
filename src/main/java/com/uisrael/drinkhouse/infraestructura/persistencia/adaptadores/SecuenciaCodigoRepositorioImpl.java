package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.SecuenciaCodigo;
import com.uisrael.drinkhouse.dominio.repositorios.ISecuenciaCodigoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.SecuenciaCodigoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ISecuenciaCodigoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ISecuenciaCodigoJpaRepositorio;

public class SecuenciaCodigoRepositorioImpl implements ISecuenciaCodigoRepositorio {

	private final ISecuenciaCodigoJpaRepositorio jpaRepositorio;
	private final ISecuenciaCodigoJpaMapper secuenciaCodigoMapper;

	public SecuenciaCodigoRepositorioImpl(ISecuenciaCodigoJpaRepositorio jpaRepositorio,
			ISecuenciaCodigoJpaMapper secuenciaCodigoMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.secuenciaCodigoMapper = secuenciaCodigoMapper;
	}

	@Override
	public SecuenciaCodigo guardar(SecuenciaCodigo secuenciacodigo) {

		SecuenciaCodigoEntity entity = secuenciaCodigoMapper.toEntity(secuenciacodigo);
		SecuenciaCodigoEntity guardado = jpaRepositorio.save(entity);

		return secuenciaCodigoMapper.toDomain(guardado);
	}

	@Override
	public Optional<SecuenciaCodigo> buscarPorId(int id) {
		
		return jpaRepositorio.findById(id).map(secuenciaCodigoMapper::toDomain);
	}

	@Override
	public List<SecuenciaCodigo> listarTodos() {
		// TODO Auto-generated method stub
		return jpaRepositorio.findAll().stream().map(secuenciaCodigoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		
		jpaRepositorio.deleteById(id);

	}

}
