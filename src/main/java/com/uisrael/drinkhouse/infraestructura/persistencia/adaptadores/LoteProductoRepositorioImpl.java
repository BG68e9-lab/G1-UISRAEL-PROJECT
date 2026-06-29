package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ILoteProductoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.LoteProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ILoteProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ILoteProductoJpaRepositorio;

public class LoteProductoRepositorioImpl implements ILoteProductoRepositorio {

	private final ILoteProductoJpaRepositorio jpaRepositorio;
	private final ILoteProductoJpaMapper loteProductoMapper;

	public LoteProductoRepositorioImpl(ILoteProductoJpaRepositorio jpaRepositorio, ILoteProductoJpaMapper loteProductoMapper) {

		this.jpaRepositorio = jpaRepositorio;
		this.loteProductoMapper = loteProductoMapper;
	}

	@Override
	public LoteProducto guardar(LoteProducto loteProducto) {

		LoteProductoEntity entity = loteProductoMapper.toEntity(loteProducto);
		LoteProductoEntity guardado = jpaRepositorio.save(entity);
		return loteProductoMapper.toDomain(guardado);
	}

	@Override
	public Optional<LoteProducto> buscarPorId(int id) {
		return jpaRepositorio.findById(id).map(loteProductoMapper::toDomain);
	}

	@Override
	public List<LoteProducto> listarTodos() {
		return jpaRepositorio.findAll().stream().map(loteProductoMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		jpaRepositorio.deleteById(id);

	}

}
