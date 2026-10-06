package com.veterinaria.expedientes.client;

import com.veterinaria.expedientes.dto.CitaResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "citas-service", url = "http://localhost:8082")
public interface CitaFeignClient {

    @PatchMapping("/api/v1/citas/{id}/completar")
    CitaResponseDTO completarCita(@PathVariable("id") Long id);
}

