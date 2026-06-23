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
@Table(name = "consumos_ia_mensual")
@Getter
@Setter
public class ConsumoIaMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes_anio", nullable = false)
    private String mesAnio;

    @Column(name = "tokens_consumidos", nullable = false)
    private Long tokensConsumidos;

    @Column(name = "costo_estimado", nullable = false)
    private Double costoEstimado;

    @Column(name = "fecha_actualizacion", nullable = false)
    private OffsetDateTime fechaActualizacion;

    public ConsumoIaMensual() {
    }
}