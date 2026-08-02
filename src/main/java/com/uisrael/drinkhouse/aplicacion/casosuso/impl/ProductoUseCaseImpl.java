package com.uisrael.drinkhouse.aplicacion.casosuso.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoIceUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoPrecioHistoricoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.IProductoUseCase;
import com.uisrael.drinkhouse.aplicacion.casosuso.entrada.ITasaIvaUseCase;
import com.uisrael.drinkhouse.dominio.entidades.Producto;
import com.uisrael.drinkhouse.dominio.entidades.ProductoIceHistorico;
import com.uisrael.drinkhouse.dominio.entidades.TasaIva;
import com.uisrael.drinkhouse.dominio.repositorios.IProductoRepositorio;

public class ProductoUseCaseImpl implements IProductoUseCase{

	private final IProductoRepositorio repositorio;
	private final ITasaIvaUseCase tasaIvaUseCase;
	private final IProductoIceUseCase productoIceUseCase;
	private final IProductoPrecioHistoricoUseCase precioHistoricoUseCase;

	public ProductoUseCaseImpl(IProductoRepositorio repositorio, ITasaIvaUseCase tasaIvaUseCase,
			IProductoIceUseCase productoIceUseCase, IProductoPrecioHistoricoUseCase precioHistoricoUseCase) {
		this.repositorio = repositorio;
		this.tasaIvaUseCase = tasaIvaUseCase;
		this.productoIceUseCase = productoIceUseCase;
		this.precioHistoricoUseCase = precioHistoricoUseCase;
	}

	@Override
	public Producto crear(Producto producto) {

		resolverPrecioVenta(producto);
		Producto guardado = repositorio.guardar(producto);
		registrarSnapshotPrecio(guardado, "Creacion de producto");
		return guardado;
	}

	@Override
	public Producto actualizar(int id, Producto producto) {
		// Se captura el estado anterior para saber si realmente cambio el precio
		// (y por lo tanto si corresponde generar un nuevo registro historico).
		Producto anterior = buscarPorId(id);

		resolverPrecioVenta(producto);
		Producto actualizado = repositorio.actualizar(id, producto);

		if (cambioPrecio(anterior, actualizado)) {
			registrarSnapshotPrecio(actualizado, "Actualizacion de producto");
		}

		return actualizado;
	}

	private boolean cambioPrecio(Producto anterior, Producto nuevo) {
		return !valoresIguales(anterior.getCostoPromedio(), nuevo.getCostoPromedio())
				|| !valoresIguales(anterior.getMargenGanancia(), nuevo.getMargenGanancia())
				|| !valoresIguales(anterior.getPrecioVenta(), nuevo.getPrecioVenta());
	}

	private boolean valoresIguales(BigDecimal a, BigDecimal b) {
		if (a == null || b == null) {
			return a == b;
		}
		return a.compareTo(b) == 0;
	}

	/**
	 * Registra un snapshot consolidado en el historico de precios, tomando la
	 * tasa de IVA vigente (global, 0 si el producto esta exento o si todavia
	 * no se ha configurado ninguna tasa) y la tasa de ICE vigente del producto
	 * (si tiene alguna configurada).
	 */
	private void registrarSnapshotPrecio(Producto producto, String motivo) {
		BigDecimal ivaPorcentaje = Boolean.TRUE.equals(producto.getIvaExento()) ? BigDecimal.ZERO
				: tasaIvaUseCase.obtenerVigente().map(TasaIva::getPorcentaje).orElse(BigDecimal.ZERO);

		Optional<ProductoIceHistorico> iceVigente = productoIceUseCase.obtenerVigente(producto.getProductoId());
		String iceTipo = iceVigente.map(ProductoIceHistorico::getTipoIce).orElse(null);
		BigDecimal iceValor = iceVigente.map(ProductoIceHistorico::getValor).orElse(null);

		precioHistoricoUseCase.registrarSnapshot(producto.getProductoId(), producto.getCostoPromedio(),
				producto.getMargenGanancia(), producto.getPrecioVenta(), ivaPorcentaje, iceTipo, iceValor, motivo);
	}

	/**
	 * Si el producto no usa precio personalizado, calcula precioVenta =
	 * costoPromedio + (costoPromedio * margenGanancia / 100). Si usa precio
	 * personalizado, exige que el cliente haya enviado un precioVenta valido.
	 */
	private void resolverPrecioVenta(Producto producto) {
		boolean personalizado = Boolean.TRUE.equals(producto.getPrecioPersonalizado());

		if (personalizado) {
			if (producto.getPrecioVenta() == null || producto.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
				throw new IllegalArgumentException(
						"El precio de venta es obligatorio y debe ser mayor a cero cuando se usa precio personalizado");
			}
			return;
		}

		if (producto.getCostoPromedio() == null) {
			throw new IllegalArgumentException("El costo promedio es obligatorio");
		}

		BigDecimal margen = producto.getMargenGanancia() != null ? producto.getMargenGanancia() : BigDecimal.ZERO;
		BigDecimal factor = BigDecimal.ONE.add(margen.divide(BigDecimal.valueOf(100)));
		BigDecimal precioCalculado = producto.getCostoPromedio().multiply(factor).setScale(2, RoundingMode.HALF_UP);

		producto.setPrecioVenta(precioCalculado);
	}

	@Override
	public Producto buscarPorId(int id) {

		return repositorio.buscarPorId(id)
				.orElseThrow(()->new RuntimeException("Producto no encontrado"));
	}

	@Override
	public List<Producto> listar() {

		return repositorio.listarTodos();
	}

	@Override
	public void eliminar(int id) {

		repositorio.eliminar(id);

	}

	@Override
	public List<Producto> buscar(String nombre, String marca) {
		String nombreFiltro = normalizar(nombre);
		String marcaFiltro = normalizar(marca);

		return repositorio.listarTodos().stream()
				.filter(p -> nombreFiltro == null || contiene(p.getNombre(), nombreFiltro))
				.filter(p -> marcaFiltro == null || contiene(p.getMarca(), marcaFiltro))
				.toList();
	}

	private String normalizar(String valor) {
		return (valor == null || valor.isBlank()) ? null : valor.trim().toLowerCase();
	}

	private boolean contiene(String valor, String filtro) {
		return valor != null && valor.toLowerCase().contains(filtro);
	}

}
