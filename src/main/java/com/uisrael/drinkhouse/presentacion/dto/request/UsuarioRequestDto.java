package com.uisrael.drinkhouse.presentacion.dto.request;

import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UsuarioRequestDto {

    @NotBlank
    private UUID negocioId;

    @NotBlank
    private UUID rolId;

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