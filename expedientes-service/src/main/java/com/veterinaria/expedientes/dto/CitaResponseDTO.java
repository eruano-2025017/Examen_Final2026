package com.veterinaria.expedientes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CitaResponseDTO {

    private Long id;
    private Long mascotaId;
    private Long veterinarioId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime fechaHora;

    private String motivo;
    private String estado;
}

