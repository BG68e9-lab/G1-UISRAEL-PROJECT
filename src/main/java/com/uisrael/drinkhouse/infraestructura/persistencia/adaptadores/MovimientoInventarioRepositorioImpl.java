package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.MovimientoInventario;
import com.uisrael.drinkhouse.dominio.repositorios.IMovimientoInventarioRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.MovimientoInventarioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IMovimientoInventarioJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IMovimientoInventarioJpaRepositorio;

public class MovimientoInventarioRepositorioImpl implements IMovimientoInventarioRepositorio {

	private final IMovimientoInventarioJpaRepositorio jpaRepositorio;
	private final IMovimientoInventarioJpaMapper movimientoInventarioMapper;

	public MovimientoInventarioRepositorioImpl(IMovimientoInventarioJpaRepositorio jpaRepositorio,
			IMovimientoInventarioJpaMapper movimientoInventarioMapper) {

		this.jpaRepositorio = jpaRepositorio;
		this.movimientoInventarioMapper = movimientoInventarioMapper;
	}

	@Override
	public MovimientoInventario guardar(MovimientoInventario movimientoinventario) {
		MovimientoInventarioEntity entity = movimientoInventarioMapper.toEntity(movimientoinventario);
		MovimientoInventarioEntity guardado = jpaRepositorio.save(entity);
		return movimientoInventarioMapper.toDomain(guardado);
	}

	@Override
	public Optional<MovimientoInventario> buscarPorId(int id) {
		
		return jpaRepositorio.findById(id).map(movimientoInventarioMapper::toDomain);
	}

	@Override
	public List<MovimientoInventario> listarTodo() {

		return jpaRepositorio.findAll().stream().map(movimientoInventarioMapper::toDomain).toList();
	}

	@Override
	public void eliminar(int id) {
		
		jpaRepositorio.deleteById(id);
	

	}

}
