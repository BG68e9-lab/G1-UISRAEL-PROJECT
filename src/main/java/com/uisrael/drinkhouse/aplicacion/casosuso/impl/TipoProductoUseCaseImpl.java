package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITipoProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.entidades.TipoProducto;
import com.uisrael.drinkhouse.dominio.repositorios.ICategoriaRepositorio;
import com.uisrael.drinkhouse.dominio.repositorios.ITipoProductoRepositorio;

public class TipoProductoUseCaseImpl implements ITipoProductoUseCase {

	private final ITipoProductoRepositorio tipoProductoRepositorio;
	private final ICategoriaRepositorio categoriaRepositorio;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;

	public TipoProductoUseCaseImpl(
			ITipoProductoRepositorio tipoProductoRepositorio,
			ICategoriaRepositorio categoriaRepositorio,
			ILogAuditoriaUseCase logAuditoriaUseCase) {
		this.tipoProductoRepositorio = tipoProductoRepositorio;
		this.categoriaRepositorio = categoriaRepositorio;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
	}

	@Override
	public TipoProducto crear(TipoProducto tipoProducto) {
		categoriaRepositorio.buscarPorId(tipoProducto.getCategoriaId())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Categoría no encontrada con id: " + tipoProducto.getCategoriaId()));

		if (tipoProductoRepositorio.existePorNombreYCategoria(
				tipoProducto.getNombre(), tipoProducto.getCategoriaId())) {
			throw new ConflictoUnicoException(
					"Ya existe un tipo de producto con nombre '" + tipoProducto.getNombre() 
					+ "' en esta categoría");
		}

		if (tipoProducto.getActivo() == null) {
			tipoProducto.setActivo(true);
		}

		TipoProducto guardado = tipoProductoRepositorio.guardar(tipoProducto);
		logAuditoriaUseCase.registrar("TipoProducto", guardado.getTipoProductoId().toString(), 
				"CREAR", guardado);
		return guardado;
	}

	@Override
	public TipoProducto actualizar(TipoProducto tipoProducto) {
		TipoProducto existente = tipoProductoRepositorio.buscarPorId(tipoProducto.getTipoProductoId())
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Tipo de producto no encontrado con id: " + tipoProducto.getTipoProductoId()));

		if (!existente.getCategoriaId().equals(tipoProducto.getCategoriaId())) {
			categoriaRepositorio.buscarPorId(tipoProducto.getCategoriaId())
					.orElseThrow(() -> new RecursoNoEncontradoException(
							"Categoría no encontrada con id: " + tipoProducto.getCategoriaId()));

			if (tipoProductoRepositorio.tieneProductosAsociados(tipoProducto.getTipoProductoId())) {
				throw new ReglaNegocioException(
						"No se puede cambiar la categoría porque tiene productos asociados");
			}
		}

		existente.setCategoriaId(tipoProducto.getCategoriaId());
		existente.setNombre(tipoProducto.getNombre());
		existente.setDescripcion(tipoProducto.getDescripcion());
		
		if (tipoProducto.getActivo() != null) {
			existente.setActivo(tipoProducto.getActivo());
		}

		TipoProducto actualizado = tipoProductoRepositorio.guardar(existente);
		logAuditoriaUseCase.registrar("TipoProducto", actualizado.getTipoProductoId().toString(), 
				"ACTUALIZAR", actualizado);
		return actualizado;
	}

	@Override
	public Optional<TipoProducto> buscarPorId(Long id) {
		return tipoProductoRepositorio.buscarPorId(id);
	}

	@Override
	public List<TipoProducto> listarTodos() {
		return tipoProductoRepositorio.listarTodos();
	}

	@Override
	public List<TipoProducto> listarPorCategoria(Long categoriaId) {
		categoriaRepositorio.buscarPorId(categoriaId)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Categoría no encontrada con id: " + categoriaId));
		
		return tipoProductoRepositorio.listarPorCategoria(categoriaId);
	}

	@Override
	public void desactivar(Long id) {
		TipoProducto tipoProducto = tipoProductoRepositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Tipo de producto no encontrado con id: " + id));
		
		tipoProducto.setActivo(false);
		tipoProductoRepositorio.guardar(tipoProducto);
		logAuditoriaUseCase.registrar("TipoProducto", id.toString(), "DESACTIVAR", tipoProducto);
	}

	@Override
	public void eliminar(Long id) {
		tipoProductoRepositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException(
						"Tipo de producto no encontrado con id: " + id));

		if (tipoProductoRepositorio.tieneProductosAsociados(id)) {
			throw new ReglaNegocioException(
					"No se puede eliminar el tipo de producto porque tiene productos asociados");
		}

		tipoProductoRepositorio.eliminar(id);
		logAuditoriaUseCase.registrar("TipoProducto", id.toString(), "ELIMINAR", null);
	}
}
