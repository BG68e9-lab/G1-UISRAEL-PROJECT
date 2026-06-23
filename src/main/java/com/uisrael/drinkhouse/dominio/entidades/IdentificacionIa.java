package com.uisrael.drinkhouse.dominio.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "identificaciones_ia")
@Getter
@Setter
public class IdentificacionIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_modelo", nullable = false)
    private String nombreModelo;

    @Column(name = "probabilidad", nullable = false)
    private Double probabilidad;

    @Column(name = "resultado", nullable = false)
    private String resultado;

    public IdentificacionIa() {
    }
}