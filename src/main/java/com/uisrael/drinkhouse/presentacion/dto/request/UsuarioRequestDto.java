package com.uisrael.drinkhouse.presentacion.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UsuarioRequestDto {

    @NotNull
    private Integer negocioId;

    @NotNull
    private Integer rolId;

    @NotBlank
    private String nombres;

    @NotBlank
    private String apellidos;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String proveedorSso;

    private String ssoSubjectId;
}