package com.uisrael.drinkhouse.dominio.entidades;

import java.math.BigDecimal;
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
@Table(name = "ORDENES_COMPRA")
@Getter
@Setter
public class OrdenCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orden_compra")
    private Long idOrdenCompra;

    @Column(name = "codigo_orden", nullable = false, unique = true, length = 50)
    private String codigoOrden;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private OffsetDateTime fechaCreacion;

    // Constructor vacío obligatorio para JPA (¡Sin redundancias de super()!)
    public OrdenCompra() {
    }

    // Método automático para registrar la fecha exacta con zona horaria antes de guardar
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = OffsetDateTime.now();
    }
}