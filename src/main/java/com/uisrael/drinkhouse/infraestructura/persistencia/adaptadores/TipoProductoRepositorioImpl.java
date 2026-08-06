package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoProductoRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.TipoProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.ITipoProductoJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.ITipoProductoJpaRepositorio;

public class TipoProductoRepositorioImpl implements ITipoProductoRepositorio {

	private final ITipoProductoJpaRepositorio jpaRepositorio;
	private final ITipoProductoJpaMapper tipoProductoMapper;

	public TipoProductoRepositorioImpl(
			ITipoProductoJpaRepositorio jpaRepositorio,
			ITipoProductoJpaMapper tipoProductoMapper) {
		this.jpaRepositorio = jpaRepositorio;
		this.tipoProductoMapper = tipoProductoMapper;
	}

	@Override
	public TipoProducto guardar(TipoProducto tipoProducto) {
		TipoProductoEntity entity = tipoProductoMapper.toEntity(tipoProducto);
		TipoProductoEntity guardado = jpaRepositorio.save(entity);
		return tipoProductoMapper.toDomain(guardado);
	}

	@Override
	public Optional<TipoProducto> buscarPorId(Long id) {
		return jpaRepositorio.findById(id).map(tipoProductoMapper::toDomain);
	}

	@Override
	public List<TipoProducto> listarTodos() {
		return jpaRepositorio.findAll().stream()
				.map(tipoProductoMapper::toDomain)
				.toList();
	}

	@Override
	public List<TipoProducto> listarPorCategoria(Long categoriaId) {
		return jpaRepositorio.findByCategoriaId(categoriaId).stream()
				.map(tipoProductoMapper::toDomain)
				.toList();
	}

	@Override
	public void eliminar(Long id) {
		jpaRepositorio.deleteById(id);
	}

	@Override
	public boolean existePorNombreYCategoria(String nombre, Long categoriaId) {
		return jpaRepositorio.existsByNombreAndCategoriaId(nombre, categoriaId);
	}

	@Override
	public boolean tieneProductosAsociados(Long id) {
		return jpaRepositorio.hasProductosAsociados(id);
	}
}
