package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que representa el resultado de una identificación de producto mediante IA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "identificaciones_ia")
public class IdentificacionIaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "identificacion_id")
    private Long identificacionId;

    @ManyToOne
    @JoinColumn(name = "negocio_id")
    private NegocioEntity negocio;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private ProductoEntity producto;

    @Column(name = "archivo_url", length = 500)
    private String archivoUrl;

    @Column(name = "modelo_ia_usado", length = 100)
    private String modeloIaUsado;

    @Column(name = "nombre_sugerido", length = 200)
    private String nombreSugerido;

    @Column(name = "marca_sugerida", length = 100)
    private String marcaSugerida;

    @Column(name = "tipo_sugerido", length = 100)
    private String tipoSugerido;

    @Column(name = "reconocido", nullable = false)
    private Boolean reconocido;

    @ManyToOne
    @JoinColumn(name = "orden_compra_relacionada")
    private OrdenCompraEntity ordenCompraRelacionada;

    @Column(name = "confirmado_por")
    private UUID confirmadoPor;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private OffsetDateTime creadoEn;

    @PrePersist
    protected void onCreate() {
        if (this.creadoEn == null) {
            this.creadoEn = OffsetDateTime.now();
        }
        if (this.reconocido == null) {
            this.reconocido = false;
        }
    }
}
