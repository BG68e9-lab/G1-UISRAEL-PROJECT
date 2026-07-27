package com.uisrael.drinkhouse.infraestructura.persistencia.jpa;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad JPA que define el token de IA activo para un negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tokens_ia_negocio")
public class TokensIaNegocioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_ia_id")
    private UUID tokenIaId;

    @OneToOne
    @JoinColumn(name = "negocio_id", nullable = false, unique = true)
    private NegocioEntity negocio;

    @Column(name = "token_hash", nullable = false, length = 255)
    private String tokenHash;

    @Column(name = "activo", nullable = false)
    private Boolean activo;
}
