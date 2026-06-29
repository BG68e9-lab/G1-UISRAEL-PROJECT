package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.LoteProducto;

public interface ILoteProductoRepositorio {

	LoteProducto guardar(LoteProducto loteProducto);

	Optional<LoteProducto> buscarPorId(int id);

	List<LoteProducto> listarTodos();

	void eliminar(int id);

}
