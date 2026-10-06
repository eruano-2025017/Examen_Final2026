package com.veterinaria.auth.dto;

import com.veterinaria.auth.entity.Rol;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String token;
    private Long id;
    private String nombre;
    private String email;
    private Rol rol;
}

