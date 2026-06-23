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
@Table(name = "tokens_ia_negocio")
@Getter
@Setter
public class TokensIaNegocio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "clave_configuracion", nullable = false, unique = true)
    private String claveConfiguracion;

    @Column(name = "valor_token", nullable = false)
    private String valorToken;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private OffsetDateTime fechaRegistro;

    public TokensIaNegocio() {
    }
}