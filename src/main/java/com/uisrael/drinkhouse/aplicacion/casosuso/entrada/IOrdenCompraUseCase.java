package com.uisrael.drinkhouse.aplicacion.casosuso.entrada;

import java.util.List;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;

public interface IOrdenCompraUseCase {

	OrdenCompra crear(OrdenCompra ordenCompra);

	OrdenCompra actualizar(Long id, OrdenCompra ordenCompra);

	OrdenCompra buscarPorId(Long id);

	OrdenCompra buscarPorCodigo(String codigoReferencia);

	List<OrdenCompra> listar(String estado);

	OrdenCompra cambiarEstado(Long id, String nuevoEstado);

	OrdenCompra recibir(Long id);

	void eliminar(Long id);
}
