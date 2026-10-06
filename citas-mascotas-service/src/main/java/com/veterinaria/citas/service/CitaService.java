package com.veterinaria.citas.service;

import com.veterinaria.citas.dto.CitaRequestDTO;
import com.veterinaria.citas.dto.CitaResponseDTO;
import com.veterinaria.citas.entity.CitaMedica;
import com.veterinaria.citas.entity.Mascota;
import com.veterinaria.citas.enums.EstadoCita;
import com.veterinaria.citas.exception.BusinessException;
import com.veterinaria.citas.repository.CitaRepository;
import com.veterinaria.citas.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CitaService {

    private final CitaRepository citaRepository;
    private final MascotaRepository mascotaRepository;

    @Transactional
    public CitaResponseDTO agendarCita(CitaRequestDTO dto, Long clienteAutenticadoId, String rol) {
        Mascota mascota = mascotaRepository.findById(dto.getMascotaId())
                .orElseThrow(() -> new BusinessException("Mascota no encontrada con id: " + dto.getMascotaId()));

        if ("CLIENTE".equals(rol) && !mascota.getClienteId().equals(clienteAutenticadoId)) {
            throw new BusinessException("No tienes autorización para agendar citas para una mascota que no te pertenece");
        }

        LocalDateTime fechaHora = dto.getFechaHora();
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new BusinessException("La fecha y hora de la cita debe ser en el futuro");
        }

        // =========================================================================
        // REGLA 1: Disponibilidad Vet (Citas fijas de 30 minutos sin solapamiento)
        // =========================================================================
        LocalDateTime inicioVentana = fechaHora.minusMinutes(30);
        LocalDateTime finVentana = fechaHora.plusMinutes(30);

        boolean solapa = citaRepository.existsSolapamientoVeterinario(
                dto.getVeterinarioId(),
                inicioVentana,
                finVentana,
                EstadoCita.CANCELADA
        );

        if (solapa) {
            throw new BusinessException("El veterinario no tiene disponibilidad en el horario solicitado (citas de 30 min sin solapamiento)");
        }

        // =========================================================================
        // REGLA 2: Límite Cliente (Máximo 2 citas PENDIENTES el mismo día)
        // =========================================================================
        LocalDateTime inicioDia = fechaHora.toLocalDate().atStartOfDay();
        LocalDateTime finDia = fechaHora.toLocalDate().atTime(23, 59, 59);

        long citasPendientesMismoDia = citaRepository.countCitasPendientesClienteEnFecha(
                mascota.getClienteId(),
                EstadoCita.PENDIENTE,
                inicioDia,
                finDia
        );

        if (citasPendientesMismoDia >= 2) {
            throw new BusinessException("El cliente ya cuenta con el límite de 2 citas en estado PENDIENTE para el día " + fechaHora.toLocalDate());
        }

        CitaMedica nuevaCita = CitaMedica.builder()
                .mascotaId(dto.getMascotaId())
                .veterinarioId(dto.getVeterinarioId())
                .fechaHora(fechaHora)
                .motivo(dto.getMotivo())
                .estado(EstadoCita.PENDIENTE)
                .build();

        CitaMedica guardada = citaRepository.save(nuevaCita);
        return toDTO(guardada);
    }

    @Transactional
    public CitaResponseDTO cancelarCita(Long citaId, Long usuarioAutenticadoId, String rol) {
        CitaMedica cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new BusinessException("Cita médica no encontrada con id: " + citaId));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new BusinessException("La cita ya se encuentra en estado CANCELADA");
        }

        if (cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new BusinessException("No es posible cancelar una cita médica que ya ha sido COMPLETADA");
        }

        if ("CLIENTE".equals(rol)) {
            Mascota mascota = mascotaRepository.findById(cita.getMascotaId())
                    .orElseThrow(() -> new BusinessException("Mascota no encontrada para la cita"));
            if (!mascota.getClienteId().equals(usuarioAutenticadoId)) {
                throw new BusinessException("No tienes permiso para cancelar esta cita médica");
            }
        }

        // =========================================================================
        // REGLA 3: Cancelación con MÁS de 2 horas de anticipación
        // =========================================================================
        LocalDateTime ahora = LocalDateTime.now();
        if (!cita.getFechaHora().isAfter(ahora.plusHours(2))) {
            throw new BusinessException("La cita solo se puede cancelar con MÁS de 2 horas de anticipación a la hora programada");
        }

        cita.setEstado(EstadoCita.CANCELADA);
        CitaMedica actualizada = citaRepository.save(cita);
        return toDTO(actualizada);
    }

    @Transactional
    public CitaResponseDTO completarCita(Long citaId) {
        CitaMedica cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new BusinessException("Cita médica no encontrada con id: " + citaId));

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new BusinessException("No se puede completar una cita que está cancelada");
        }

        cita.setEstado(EstadoCita.COMPLETADA);
        return toDTO(citaRepository.save(cita));
    }

    @Transactional(readOnly = true)
    public List<CitaResponseDTO> obtenerAgenda(Long veterinarioId, Long usuarioAutenticadoId, String rol) {
        Long vetIdFiltro;
        if ("VET".equals(rol)) {
            vetIdFiltro = usuarioAutenticadoId;
        } else {
            vetIdFiltro = veterinarioId;
        }

        LocalDateTime hoy = LocalDateTime.now().toLocalDate().atStartOfDay();
        return citaRepository.findAgendaVeterinario(vetIdFiltro, hoy).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public CitaResponseDTO obtenerPorId(Long id) {
        return citaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new BusinessException("Cita no encontrada con id: " + id));
    }

    private CitaResponseDTO toDTO(CitaMedica c) {
        return CitaResponseDTO.builder()
                .id(c.getId())
                .mascotaId(c.getMascotaId())
                .veterinarioId(c.getVeterinarioId())
                .fechaHora(c.getFechaHora())
                .motivo(c.getMotivo())
                .estado(c.getEstado())
                .build();
    }
}

