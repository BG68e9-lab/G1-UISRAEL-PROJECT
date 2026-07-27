package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Alerta;
import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;
import com.uisrael.drinkhouse.dominio.entidades.Producto;

public interface IAlertaUseCase {

	void crearAlertaStockBajo(Producto producto);

	void crearAlertaVencimientoProximo(LoteProducto lote);

	List<Alerta> listarConFiltros(String tipoAlerta, Boolean atendida);

	Alerta marcarComoAtendida(Long alertaId);

	long contarNoAtendidas();
}
