package com.veterinaria.citas.service;

import com.veterinaria.citas.dto.MascotaDTO;
import com.veterinaria.citas.entity.Mascota;
import com.veterinaria.citas.exception.BusinessException;
import com.veterinaria.citas.repository.MascotaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MascotaService {

    private final MascotaRepository mascotaRepository;

    @Transactional
    public MascotaDTO registrarMascota(MascotaDTO dto, Long clienteIdAutenticado, String rol) {
        Long clienteFinalId;
        if ("ADMIN".equals(rol) && dto.getClienteId() != null) {
            clienteFinalId = dto.getClienteId();
        } else {
            clienteFinalId = clienteIdAutenticado;
        }

        if (clienteFinalId == null) {
            throw new BusinessException("No se pudo identificar el cliente propietario de la mascota");
        }

        Mascota mascota = Mascota.builder()
                .nombre(dto.getNombre())
                .especie(dto.getEspecie())
                .raza(dto.getRaza())
                .edad(dto.getEdad())
                .clienteId(clienteFinalId)
                .build();

        Mascota guardada = mascotaRepository.save(mascota);
        return toDTO(guardada);
    }

    @Transactional(readOnly = true)
    public List<MascotaDTO> obtenerMisMascotas(Long clienteId) {
        if (clienteId == null) {
            throw new BusinessException("Identificador de cliente no proporcionado");
        }
        return mascotaRepository.findByClienteId(clienteId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public MascotaDTO obtenerPorId(Long id) {
        return mascotaRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new BusinessException("Mascota no encontrada con id: " + id));
    }

    private MascotaDTO toDTO(Mascota m) {
        return MascotaDTO.builder()
                .id(m.getId())
                .nombre(m.getNombre())
                .especie(m.getEspecie())
                .raza(m.getRaza())
                .edad(m.getEdad())
                .clienteId(m.getClienteId())
                .build();
    }
}

