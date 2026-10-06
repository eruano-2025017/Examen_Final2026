package com.veterinaria.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;
}

