package com.veterinaria.citas.repository;

import com.veterinaria.citas.entity.CitaMedica;
import com.veterinaria.citas.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<CitaMedica, Long> {

    List<CitaMedica> findByVeterinarioIdOrderByFechaHoraAsc(Long veterinarioId);

    List<CitaMedica> findByMascotaId(Long mascotaId);

    /**
     * Consulta 1: Solapamiento de citas de 30 minutos por veterinarioId.
     * Si la cita solicitada inicia en T, solapa si existe otra cita con fechaHora en (T - 30min, T + 30min)
     * que no esté cancelada.
     */
    @Query("SELECT COUNT(c) > 0 FROM CitaMedica c " +
           "WHERE c.veterinarioId = :veterinarioId " +
           "AND c.estado != :estadoCancelada " +
           "AND c.fechaHora > :inicioVentana " +
           "AND c.fechaHora < :finVentana")
    boolean existsSolapamientoVeterinario(
            @Param("veterinarioId") Long veterinarioId,
            @Param("inicioVentana") LocalDateTime inicioVentana,
            @Param("finVentana") LocalDateTime finVentana,
            @Param("estadoCancelada") EstadoCita estadoCancelada);

    /**
     * Consulta 2: Conteo de citas PENDIENTES por clienteId para la misma fecha.
     */
    @Query("SELECT COUNT(c) FROM CitaMedica c " +
           "WHERE c.mascota.clienteId = :clienteId " +
           "AND c.estado = :estado " +
           "AND c.fechaHora >= :inicioDia " +
           "AND c.fechaHora <= :finDia")
    long countCitasPendientesClienteEnFecha(
            @Param("clienteId") Long clienteId,
            @Param("estado") EstadoCita estado,
            @Param("inicioDia") LocalDateTime inicioDia,
            @Param("finDia") LocalDateTime finDia);

    /**
     * Agenda de citas del veterinario (o todas las citas si es ADMIN) ordenadas por fecha/hora.
     */
    @Query("SELECT c FROM CitaMedica c " +
           "WHERE (:veterinarioId IS NULL OR c.veterinarioId = :veterinarioId) " +
           "AND c.fechaHora >= :desde " +
           "ORDER BY c.fechaHora ASC")
    List<CitaMedica> findAgendaVeterinario(
            @Param("veterinarioId") Long veterinarioId,
            @Param("desde") LocalDateTime desde);
}

