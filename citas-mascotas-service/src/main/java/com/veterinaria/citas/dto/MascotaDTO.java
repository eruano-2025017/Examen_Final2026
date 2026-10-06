package com.veterinaria.citas.dto;

import com.veterinaria.citas.enums.Especie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MascotaDTO {

    private Long id;

    @NotBlank(message = "El nombre de la mascota es obligatorio")
    private String nombre;

    @NotNull(message = "La especie es obligatoria")
    private Especie especie;

    private String raza;

    private Integer edad;

    private Long clienteId;
}

