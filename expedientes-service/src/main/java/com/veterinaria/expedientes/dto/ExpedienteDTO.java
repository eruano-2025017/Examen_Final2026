package com.veterinaria.expedientes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpedienteDTO {

    private Long id;
    private Long citaId;
    private Long mascotaId;
    private String diagnostico;
    private String tratamiento;
    private Double pesoKg;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaRegistro;
}

