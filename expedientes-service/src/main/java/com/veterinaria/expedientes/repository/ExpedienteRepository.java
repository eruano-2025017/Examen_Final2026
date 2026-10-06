package com.veterinaria.expedientes.repository;

import com.veterinaria.expedientes.entity.ExpedienteClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedienteRepository extends JpaRepository<ExpedienteClinico, Long> {

    Optional<ExpedienteClinico> findByCitaId(Long citaId);

    boolean existsByCitaId(Long citaId);

    List<ExpedienteClinico> findByMascotaId(Long mascotaId);
}

