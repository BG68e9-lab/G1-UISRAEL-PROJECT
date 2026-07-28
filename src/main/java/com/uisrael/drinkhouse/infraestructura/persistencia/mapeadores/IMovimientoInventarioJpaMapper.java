package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

/**
 * NO USAR: mapeo reemplazado por logica manual en
 * MovimientoInventarioRepositorioImpl (el dominio usa campos planos como
 * tipo/productoId/loteId mientras que la entidad JPA usa relaciones
 * @ManyToOne, lo cual requiere resolucion manual). Se deja este archivo
 * vacio en vez de eliminarlo porque el sistema de archivos no permite
 * borrarlo desde este entorno; no se instancia ni se inyecta en ningun
 * lado.
 */
public interface IMovimientoInventarioJpaMapper {
}
