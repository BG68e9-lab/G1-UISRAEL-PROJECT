package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.IdentificacionIaEntity;

@Mapper(componentModel = "spring")
public interface IIdentificacionIaJpaMapper {

    @Mapping(source = "negocio.negocioId", target = "negocioId")
    @Mapping(source = "producto.productoId", target = "productoId")
    @Mapping(source = "ordenCompraRelacionada.ordenCompraId", target = "ordenCompraRelacionadaId")
    IdentificacionIa aDominio(IdentificacionIaEntity entidad);

    @Mapping(target = "negocio", ignore = true)
    @Mapping(target = "producto", ignore = true)
    @Mapping(target = "ordenCompraRelacionada", ignore = true)
    IdentificacionIaEntity aEntidad(IdentificacionIa dominio);
}
