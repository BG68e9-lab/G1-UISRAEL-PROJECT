package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.LocalDate;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.ConsumoIaMensual;

public interface IConsumoIaMensualRepositorio {

    ConsumoIaMensual guardar(ConsumoIaMensual consumo);

    Optional<ConsumoIaMensual> buscarPorNegocioYPeriodo(Integer negocioId, LocalDate periodo);
}
