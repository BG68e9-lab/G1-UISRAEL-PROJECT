package com.uisrael.drinkhouse.infraestructura.persistencia.mapeadores;

/**
 * NO USAR: mapeo reemplazado por logica manual en
 * LoteProductoRepositorioImpl (el dominio usa LocalDate para fechaIngreso
 * mientras que la entidad JPA usa OffsetDateTime, lo cual MapStruct no
 * puede convertir automaticamente). Se deja este archivo vacio en vez de
 * eliminarlo porque el sistema de archivos no permite borrarlo desde este
 * entorno; no se instancia ni se inyecta en ningun lado.
 */
public interface ILoteProductoJpaMapper {
}
