package com.uisrael.drinkhouse.dominio.entidades;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ALERTAS")
@Getter
@Setter
public class Alerta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_alerta")
    private Long idAlerta;

    // Relación con la orden de compra que generó la alerta
    @ManyToOne
    @JoinColumn(name = "id_orden_compra", nullable = false)
    private OrdenCompra ordenCompra;

    @Column(name = "mensaje", nullable = false, length = 255)
    private String mensaje;

    @Column(name = "leido", nullable = false)
    private Boolean leido = false;

    @Column(name = "fecha_alerta", nullable = false, updatable = false)
    private OffsetDateTime fechaAlerta;

    // Constructor vacío obligatorio para JPA (¡Sin redundancias!)
    public Alerta() {
    }

    // Método automático para registrar el momento exacto en que salta la alerta
    @PrePersist
    protected void onCreate() {
        this.fechaAlerta = OffsetDateTime.now();
    }
}