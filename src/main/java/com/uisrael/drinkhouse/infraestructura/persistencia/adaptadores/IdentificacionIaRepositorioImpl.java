package com.uisrael.drinkhouse.infraestructura.persistencia.adaptadores;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;
import com.uisrael.drinkhouse.dominio.repositorios.IIdentificacionIaRepositorio;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.IdentificacionIaEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.NegocioEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.jpa.ProductoEntity;
import com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores.IIdentificacionIaJpaMapper;
import com.uisrael.drinkhouse.infraestructura.repositorio.IIdentificacionIaJpaRepositorio;

public class IdentificacionIaRepositorioImpl implements IIdentificacionIaRepositorio {

    private final IIdentificacionIaJpaRepositorio jpaRepositorio;
    private final IIdentificacionIaJpaMapper mapper;

    public IdentificacionIaRepositorioImpl(IIdentificacionIaJpaRepositorio jpaRepositorio,
                                           IIdentificacionIaJpaMapper mapper) {
        this.jpaRepositorio = jpaRepositorio;
        this.mapper = mapper;
    }

@Override
    public IdentificacionIa guardar(IdentificacionIa identificacion) {
        IdentificacionIaEntity entidad = mapper.aEntidad(identificacion);

        if (identificacion.getProductoId() != null) {
            ProductoEntity productoRef = new ProductoEntity();
            productoRef.setProductoId(identificacion.getProductoId());
            entidad.setProducto(productoRef);
        }

        if (identificacion.getNegocioId() != null) {
            NegocioEntity negocioRef = new NegocioEntity();
            negocioRef.setNegocioId(identificacion.getNegocioId());
            entidad.setNegocio(negocioRef);
        }

        IdentificacionIaEntity guardado = jpaRepositorio.save(entidad);
        return mapper.aDominio(guardado);
    }

@Override
    public Optional<IdentificacionIa> buscarPorId(Long id) {
        return jpaRepositorio.findById(id).map(mapper::aDominio);
    }

@Override
    public List<IdentificacionIa> buscarConFiltros(Long productoId, OffsetDateTime desde, OffsetDateTime hasta) {
        return jpaRepositorio.buscarConFiltros(productoId, desde, hasta)
                .stream()
                .map(mapper::aDominio)
                .toList();
    }
}
