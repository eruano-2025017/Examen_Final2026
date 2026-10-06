package com.veterinaria.expedientes.controller;

import com.veterinaria.expedientes.dto.ExpedienteDTO;
import com.veterinaria.expedientes.dto.ExpedienteRequest;
import com.veterinaria.expedientes.service.ExpedienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/expedientes")
@RequiredArgsConstructor
public class ExpedienteController {

    private final ExpedienteService expedienteService;

    @PostMapping
    public ResponseEntity<ExpedienteDTO> crearExpediente(@Valid @RequestBody ExpedienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(expedienteService.registrarExpediente(request));
    }

    @GetMapping("/mascota/{mascotaId}")
    public ResponseEntity<List<ExpedienteDTO>> obtenerPorMascota(@PathVariable Long mascotaId) {
        return ResponseEntity.ok(expedienteService.obtenerPorMascotaId(mascotaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpedienteDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(expedienteService.obtenerPorId(id));
    }

    @GetMapping("/cita/{citaId}")
    public ResponseEntity<ExpedienteDTO> obtenerPorCita(@PathVariable Long citaId) {
        return ResponseEntity.ok(expedienteService.obtenerPorCitaId(citaId));
    }

    @GetMapping
    public ResponseEntity<List<ExpedienteDTO>> obtenerTodos() {
        return ResponseEntity.ok(expedienteService.obtenerTodos());
    }
}

