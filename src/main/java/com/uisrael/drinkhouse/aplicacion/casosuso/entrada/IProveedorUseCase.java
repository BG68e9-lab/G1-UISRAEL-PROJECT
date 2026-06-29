package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;

public interface IProveedorUseCase {
	
	Proveedor crear(Proveedor proveedor);
	Proveedor buscarPorId(int id);
	List<Proveedor> listar();
	void eliminar(int id);
}
