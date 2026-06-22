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
@Table(name = "TOKENS_IA_NEGOCIO")
@Getter
@Setter
public class TokensIaNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_token_ia")
    private Long idTokenIa;

    @Column(name = "nombre_proveedor", nullable = false, length = 50)
    private String nombreProveedor; // Ejemplo: 'OpenAI', 'Google Gemini', etc.

    @Column(name = "token_acceso", nullable = false, length = 500)
    private String tokenAcceso;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private OffsetDateTime fechaRegistro;

    // Constructor vacío obligatorio para JPA (¡Sin redundancias de super()!)
    public TokensIaNegocio() {
    }

    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = OffsetDateTime.now();
    }
}