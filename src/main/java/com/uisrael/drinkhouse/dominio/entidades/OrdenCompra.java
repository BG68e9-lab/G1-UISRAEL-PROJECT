package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ordenes_compra")
@Getter
@Setter
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_referencia", nullable = false, unique = true)
    private String codigoReferencia;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "total", nullable = false)
    private Double total;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    public OrdenCompra() {
    }
}