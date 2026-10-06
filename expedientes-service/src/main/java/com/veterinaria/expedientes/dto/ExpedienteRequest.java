package com.veterinaria.expedientes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteRequest {

    @NotNull(message = "El ID de la cita es obligatorio")
    private Long citaId;

    private Long mascotaId;

    @NotBlank(message = "El diagnóstico es obligatorio")
    private String diagnostico;

    @NotBlank(message = "El tratamiento es obligatorio")
    private String tratamiento;

    @NotNull(message = "El peso en kilogramos es obligatorio")
    @Positive(message = "El peso debe ser un número positivo")
    private Double pesoKg;
}

