package com.uisrael.drinkhouse.dominio.repositorios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.ProductoPrecioHistorico;

public interface IProductoPrecioHistoricoRepositorio {

	/**
	 * Cierra el snapshot vigente del producto (si existe) y crea uno nuevo con
	 * los valores indicados, calculando el precio final con impuestos.
	 */
	ProductoPrecioHistorico registrarSnapshot(Long productoId, BigDecimal costoPromedio, BigDecimal margenGanancia,
			BigDecimal precioVenta, BigDecimal ivaPorcentajeAplicado, String iceTipoAplicado,
			BigDecimal iceValorAplicado, String motivo);

	Optional<ProductoPrecioHistorico> obtenerVigente(Long productoId);

	List<ProductoPrecioHistorico> listarHistorial(Long productoId);
}
