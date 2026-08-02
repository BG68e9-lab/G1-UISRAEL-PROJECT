package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.ProductoIceHistorico;

public interface IProductoIceUseCase {

	ProductoIceHistorico crearNuevaTasa(Long productoId, BigDecimal valor, String tipoIce, String motivo);

	Optional<ProductoIceHistorico> obtenerVigente(Long productoId);

	List<ProductoIceHistorico> listarHistorial(Long productoId);
}
