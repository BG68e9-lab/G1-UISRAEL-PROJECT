package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.Proveedor;

public interface IProveedorUseCase {

	Proveedor crearProveedor(Proveedor proveedor);

	Proveedor actualizarProveedor(Long id, Proveedor proveedor);

	Proveedor buscarPorId(Long id);

	List<Proveedor> listarProveedores();
	
	Proveedor buscarPorRuc(String ruc);
	
	Proveedor buscarOCrearPorRuc(Proveedor proveedor);
	
	void eliminarProveedor(Long id);
}
