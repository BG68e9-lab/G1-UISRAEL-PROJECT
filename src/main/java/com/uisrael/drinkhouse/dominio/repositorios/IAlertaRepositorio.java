package com.uisrael.drinkhouse.dominio.repositorios;

import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.Alerta;

public interface IAlertaRepositorio {

	Alerta guardar(Alerta alerta);

	Optional<Alerta> buscarPorId(Long id);

	List<Alerta> listarConFiltros(String tipoAlerta, Boolean atendida);

	long contarNoAtendidas();
}
