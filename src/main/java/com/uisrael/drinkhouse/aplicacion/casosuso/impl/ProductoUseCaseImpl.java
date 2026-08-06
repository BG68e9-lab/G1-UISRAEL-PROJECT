package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.util.List;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ILogAuditoriaUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.aplicacion.excepciones.ConflictoUnicoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.RecursoNoEncontradoException;
import com.uisrael.drinkhouse.aplicacion.excepciones.ReglaNegocioException;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;

public class ProductoUseCaseImpl implements IProductoUseCase {

	private final IProductoRepositorio repositorio;
	private final ILogAuditoriaUseCase logAuditoriaUseCase;

	public ProductoUseCaseImpl(IProductoRepositorio repositorio, ILogAuditoriaUseCase logAuditoriaUseCase) {
		this.repositorio = repositorio;
		this.logAuditoriaUseCase = logAuditoriaUseCase;
	}

	@Override
	public Producto crearProducto(Producto producto) {
		System.out.println("=== USE CASE (ANTES): producto.tipoProductoId = " + producto.getTipoProductoId());
		validarCostoPromedio(producto);
		if (repositorio.existePorNombre(producto.getNombre())) {
			throw new ConflictoUnicoException("Ya existe un producto con nombre: " + producto.getNombre());
		}
		producto.setActivo(true);
		if (producto.getStockActual() == null)      producto.setStockActual(0);
		if (producto.getStockMinimo() == null)      producto.setStockMinimo(0);
		if (producto.getVisibleSinStock() == null)  producto.setVisibleSinStock(false);
		if (producto.getPrecioPersonalizado() == null) producto.setPrecioPersonalizado(false);
		calcularPrecioVenta(producto);
		System.out.println("=== USE CASE (DESPUES): producto.tipoProductoId = " + producto.getTipoProductoId());
		Producto guardado = repositorio.guardar(producto);
		logAuditoriaUseCase.registrar("Producto", guardado.getProductoId().toString(), "CREAR", guardado);
		return guardado;
	}

	@Override
	public Producto actualizarProducto(Long id, Producto producto) {
		Producto existente = repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
		validarCostoPromedio(producto);
		producto.setProductoId(id);
		if (producto.getActivo() == null)
			producto.setActivo(existente.getActivo() != null ? existente.getActivo() : true);
		if (producto.getCategoriaId() == null)
			producto.setCategoriaId(existente.getCategoriaId());
		if (producto.getStockActual() == null)
			producto.setStockActual(existente.getStockActual() != null ? existente.getStockActual() : 0);
		if (producto.getStockMinimo() == null)
			producto.setStockMinimo(existente.getStockMinimo() != null ? existente.getStockMinimo() : 0);
		if (producto.getVisibleSinStock() == null)
			producto.setVisibleSinStock(existente.getVisibleSinStock() != null ? existente.getVisibleSinStock() : false);
		if (producto.getPrecioPersonalizado() == null)
			producto.setPrecioPersonalizado(existente.getPrecioPersonalizado() != null ? existente.getPrecioPersonalizado() : false);
		calcularPrecioVenta(producto);
		Producto actualizado = repositorio.guardar(producto);
		logAuditoriaUseCase.registrar("Producto", id.toString(), "ACTUALIZAR", actualizado);
		return actualizado;
	}

	@Override
	public Producto buscarPorId(Long id) {
		return repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
	}

	@Override
	public List<Producto> listarProductos() {
		return repositorio.listarTodos();
	}

	@Override
	public List<Producto> buscarConFiltros(String nombre, String marca, Long tipoProductoId, Long categoriaId) {
		return repositorio.buscarConFiltros(nombre, marca, tipoProductoId, categoriaId);
	}

	@Override
	public void eliminarProducto(Long id) {
		repositorio.buscarPorId(id)
				.orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado con id: " + id));
		repositorio.eliminar(id);
		logAuditoriaUseCase.registrar("Producto", id.toString(), "ELIMINAR", null);
	}

	public void calcularPrecioVenta(Producto producto) {
		if (!Boolean.TRUE.equals(producto.getPrecioPersonalizado())) {
			BigDecimal factor = BigDecimal.ONE.add(
					producto.getMargenGanancia().divide(BigDecimal.valueOf(100)));
			producto.setPrecioVenta(producto.getCostoPromedio().multiply(factor));
		}
	}

	private void validarCostoPromedio(Producto producto) {
		if (producto.getCostoPromedio() == null
				|| producto.getCostoPromedio().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ReglaNegocioException("El costoPromedio debe ser mayor a cero");
		}
	}
}
