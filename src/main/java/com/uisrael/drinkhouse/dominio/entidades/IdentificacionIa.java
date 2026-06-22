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
@Table(name = "IDENTIFICACIONES_IA")
@Getter
@Setter
public class IdentificacionIa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_identificacion_ia")
    private Long idIdentificacionIa;

    @Column(name = "ruta_imagen", nullable = false, length = 255)
    private String rutaImagen;

    @Column(name = "resultado_nombre", nullable = false, length = 100)
    private String resultadoNombre;

    @Column(name = "resultado_marca", length = 50)
    private String resultadoMarca;

    @Column(name = "porcentaje_certeza", nullable = false, precision = 5, scale = 2)
    private Double porcentajeCerteza;

    @Column(name = "fecha_analisis", nullable = false, updatable = false)
    private OffsetDateTime fechaAnalisis;

    // Constructor vacío obligatorio para JPA (¡Sin redundancias!)
    public IdentificacionIa() {
    }

    // Método automático para guardar el momento exacto del escaneo con la IA
    @PrePersist
    protected void onCreate() {
        this.fechaAnalisis = OffsetDateTime.now();
    }
}