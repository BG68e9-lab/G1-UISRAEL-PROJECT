package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.NotaVenta;

public interface INotaVentaRepositorio {

NotaVenta guardar(NotaVenta notaVenta);

List<NotaVenta> listarTodas();

Optional<NotaVenta> buscarPorId(Long notaId);

void eliminar(Long notaId);
}
