package com.uisrael.drinkhouse.dominio.repositorios;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import com.uisrael.drinkhouse.dominio.entidades.IdentificacionIa;

/**
 * Puerto de salida para el repositorio de IdentificacionIa.
 */
public interface IIdentificacionIaRepositorio {

    /**
     * Persiste una identificación IA y retorna la entidad guardada.
     *
     * @param identificacion entidad a guardar
     * @return entidad persistida con ID asignado
     */
    IdentificacionIa guardar(IdentificacionIa identificacion);

    /**
     * Busca una identificación IA por su identificador.
     *
     * @param id identificador
     * @return Optional con la entidad si existe
     */
    Optional<IdentificacionIa> buscarPorId(Long id);

    /**
     * Busca identificaciones IA con filtros opcionales.
     * Si productoId es null, retorna todas en el rango de fechas.
     * Si desde o hasta son null, no se aplica ese filtro de fecha.
     *
     * @param productoId ID del producto (puede ser null)
     * @param desde      fecha mínima de creación (puede ser null)
     * @param hasta      fecha máxima de creación (puede ser null)
     * @return lista de identificaciones que coincidan con los filtros
     */
    List<IdentificacionIa> buscarConFiltros(Long productoId, OffsetDateTime desde, OffsetDateTime hasta);
}
