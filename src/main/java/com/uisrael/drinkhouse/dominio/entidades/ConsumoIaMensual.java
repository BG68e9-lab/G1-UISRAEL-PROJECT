package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "CONSUMO_IA_MENSUAL")
@Getter
@Setter
public class ConsumoIaMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_consumo_ia")
    private Long idConsumoIa;

    @Column(name = "anio", nullable = false)
    private Integer anio;

    @Column(name = "mes", nullable = false)
    private Integer mes;

    @Column(name = "cantidad_consultas", nullable = false)
    private Integer cantidadConsultas = 0;

    @Column(name = "limite_maximo", nullable = false)
    private Integer limiteMaximo;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    // Constructor vacío obligatorio
    public ConsumoIaMensual() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaActualizacion = OffsetDateTime.now();
    }
}