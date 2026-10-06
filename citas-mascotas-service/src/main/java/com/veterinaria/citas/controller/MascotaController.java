package com.veterinaria.citas.controller;

import com.veterinaria.citas.dto.MascotaDTO;
import com.veterinaria.citas.service.MascotaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mascotas")
@RequiredArgsConstructor
public class MascotaController {

    private final MascotaService mascotaService;

    @GetMapping("/mis-mascotas")
    public ResponseEntity<List<MascotaDTO>> obtenerMisMascotas(HttpServletRequest request) {
        Long clienteId = (Long) request.getAttribute("authenticatedUserId");
        return ResponseEntity.ok(mascotaService.obtenerMisMascotas(clienteId));
    }

    @PostMapping
    public ResponseEntity<MascotaDTO> registrarMascota(
            @Valid @RequestBody MascotaDTO dto,
            HttpServletRequest request) {
        Long clienteId = (Long) request.getAttribute("authenticatedUserId");
        String rol = (String) request.getAttribute("authenticatedUserRol");
        return ResponseEntity.status(HttpStatus.CREATED).body(mascotaService.registrarMascota(dto, clienteId, rol));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }
}

