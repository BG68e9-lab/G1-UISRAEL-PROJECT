package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;

public interface INotaVentaUseCase {

	
	List<NotaVenta> listarTodas();

	
	NotaVenta buscarPorId(Long notaId);
}
