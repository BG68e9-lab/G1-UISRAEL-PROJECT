package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Venta;

public interface IVentaRepositorio {

Optional<Venta> buscarPorId(Long ventaId);
}
