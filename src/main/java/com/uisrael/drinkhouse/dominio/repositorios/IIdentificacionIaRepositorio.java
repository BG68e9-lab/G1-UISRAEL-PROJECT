package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;

public interface IIdentificacionIaRepositorio {

IdentificacionIa guardar(IdentificacionIa identificacion);

Optional<IdentificacionIa> buscarPorId(Long id);

List<IdentificacionIa> buscarConFiltros(Long productoId, OffsetDateTime desde, OffsetDateTime hasta);
}
