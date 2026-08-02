package com.uisrael.drinkhouse.dominio.repositorios;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.ProductoIceHistorico;

public interface IProductoIceRepositorio {

	/**
	 * Cierra la tasa de ICE vigente del producto (si existe) y crea una nueva
	 * vigente desde ahora. Devuelve la nueva tasa.
	 */
	ProductoIceHistorico registrarNuevaTasa(Long productoId, BigDecimal valor, String tipoIce, String motivo);

	Optional<ProductoIceHistorico> obtenerVigente(Long productoId);

	List<ProductoIceHistorico> listarHistorial(Long productoId);
}
