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
@Table(name = "alertas")
@Getter
@Setter
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "mensaje", nullable = false)
    private String mensaje;

    @Column(name = "leido", nullable = false)
    private boolean leido;

    @Column(name = "fecha_alerta", nullable = false, updatable = false)
    private OffsetDateTime fechaAlerta;

    public Alerta() {
    }
}