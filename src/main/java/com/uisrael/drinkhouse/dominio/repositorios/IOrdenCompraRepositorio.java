package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.OrdenCompra;

public interface IOrdenCompraRepositorio {

	OrdenCompra guardar(OrdenCompra ordenCompra);

	Optional<OrdenCompra> buscarPorId(Long id);

	Optional<OrdenCompra> buscarPorCodigo(String codigoReferencia);

	List<OrdenCompra> listarTodos(String estado);

	OrdenCompra cambiarEstado(Long id, String nuevoEstado);

	OrdenCompra recibir(Long id);

	void eliminar(Long id);
}
