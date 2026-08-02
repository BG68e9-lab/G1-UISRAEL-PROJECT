package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.ProductoPrecioHistorico;

public interface IProductoPrecioHistoricoUseCase {

	ProductoPrecioHistorico registrarSnapshot(Long productoId, BigDecimal costoPromedio, BigDecimal margenGanancia,
			BigDecimal precioVenta, BigDecimal ivaPorcentajeAplicado, String iceTipoAplicado,
			BigDecimal iceValorAplicado, String motivo);

	Optional<ProductoPrecioHistorico> obtenerVigente(Long productoId);

	List<ProductoPrecioHistorico> listarHistorial(Long productoId);
}
