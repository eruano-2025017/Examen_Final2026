package com.veterinaria.citas.controller;

import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.service.CitaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;

    @PostMapping
    public ResponseEntity<CitaResponseDTO> agendarCita(
            @Valid @RequestBody CitaRequestDTO dto,
            HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("authenticatedUserId");
        String rol = (String) request.getAttribute("authenticatedUserRol");
        return ResponseEntity.status(HttpStatus.CREATED).body(citaService.agendarCita(dto, usuarioId, rol));
    }

    @GetMapping("/agenda")
    public ResponseEntity<List<CitaResponseDTO>> obtenerAgenda(
            @RequestParam(required = false) Long veterinarioId,
            HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("authenticatedUserId");
        String rol = (String) request.getAttribute("authenticatedUserRol");
        return ResponseEntity.ok(citaService.obtenerAgenda(veterinarioId, usuarioId, rol));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<CitaResponseDTO> cancelarCita(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long usuarioId = (Long) request.getAttribute("authenticatedUserId");
        String rol = (String) request.getAttribute("authenticatedUserRol");
        return ResponseEntity.ok(citaService.cancelarCita(id, usuarioId, rol));
    }

    @RequestMapping(value = "/{id}/completar", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<CitaResponseDTO> completarCita(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.completarCita(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitaResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(citaService.obtenerPorId(id));
    }
}

