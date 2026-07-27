package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.ConsumoIaMensual;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ConsumoIaMensualEntity;

@Mapper(componentModel = "spring")
public interface IConsumoIaMensualJpaMapper {

    @Mapping(source = "negocio.negocioId", target = "negocioId")
    ConsumoIaMensual aDominio(ConsumoIaMensualEntity entidad);

    @Mapping(target = "negocio", ignore = true)
    ConsumoIaMensualEntity aEntidad(ConsumoIaMensual dominio);
}
