package com.veterinaria.expedientes.service;

import com.veterinaria.expedientes.client.CitaFeignClient;
import com.veterinaria.expedientes.dto.CitaResponseDTO;
import com.veterinaria.expedientes.dto.ExpedienteDTO;
import com.veterinaria.expedientes.dto.ExpedienteRequest;
import com.veterinaria.expedientes.entity.ExpedienteClinico;
import com.veterinaria.expedientes.exception.BusinessException;
import com.veterinaria.expedientes.repository.ExpedienteRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExpedienteService {

    private final ExpedienteRepository expedienteRepository;
    private final CitaFeignClient citaFeignClient;

    @Transactional
    public ExpedienteDTO registrarExpediente(ExpedienteRequest request) {
        if (expedienteRepository.existsByCitaId(request.getCitaId())) {
            throw new BusinessException("Ya existe un expediente clínico registrado para la cita con ID: " + request.getCitaId());
        }

        // Llamada vía OpenFeign para cambiar el estado de la cita a COMPLETADA
        CitaResponseDTO citaCompletada;
        try {
            citaCompletada = citaFeignClient.completarCita(request.getCitaId());
        } catch (FeignException e) {
            log.error("Error al invocar citas-service vía Feign para cita ID {}: {}", request.getCitaId(), e.getMessage());
            throw new BusinessException("No fue posible completar la cita médica en citas-service: " + 
                    (e.contentUTF8().isBlank() ? e.getMessage() : e.contentUTF8()));
        }

        Long mascotaId = request.getMascotaId() != null 
                ? request.getMascotaId() 
                : (citaCompletada != null ? citaCompletada.getMascotaId() : null);

        if (mascotaId == null) {
            throw new BusinessException("No se pudo determinar el ID de la mascota asociada a la cita médica " + request.getCitaId());
        }

        ExpedienteClinico expediente = ExpedienteClinico.builder()
                .citaId(request.getCitaId())
                .mascotaId(mascotaId)
                .diagnostico(request.getDiagnostico())
                .tratamiento(request.getTratamiento())
                .pesoKg(request.getPesoKg())
                .fechaRegistro(LocalDateTime.now())
                .build();

        ExpedienteClinico guardado = expedienteRepository.save(expediente);
        return toDTO(guardado);
    }

    @Transactional(readOnly = true)
    public List<ExpedienteDTO> obtenerPorMascotaId(Long mascotaId) {
        return expedienteRepository.findByMascotaId(mascotaId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExpedienteDTO obtenerPorId(Long id) {
        return expedienteRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new BusinessException("Expediente clínico no encontrado con ID: " + id));
    }

    @Transactional(readOnly = true)
    public ExpedienteDTO obtenerPorCitaId(Long citaId) {
        return expedienteRepository.findByCitaId(citaId)
                .map(this::toDTO)
                .orElseThrow(() -> new BusinessException("Expediente clínico no encontrado para la cita ID: " + citaId));
    }

    @Transactional(readOnly = true)
    public List<ExpedienteDTO> obtenerTodos() {
        return expedienteRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private ExpedienteDTO toDTO(ExpedienteClinico entity) {
        return ExpedienteDTO.builder()
                .id(entity.getId())
                .citaId(entity.getCitaId())
                .mascotaId(entity.getMascotaId())
                .diagnostico(entity.getDiagnostico())
                .tratamiento(entity.getTratamiento())
                .pesoKg(entity.getPesoKg())
                .fechaRegistro(entity.getFechaRegistro())
                .build();
    }
}

