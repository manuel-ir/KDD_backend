package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    List<Plan> findByFechaEventoGreaterThanEqual(LocalDate fecha);

    @Query("SELECT p FROM Plan p WHERE p.fechaEvento < :hoy AND " +
           "(p.creador.id = :userId OR EXISTS (SELECT pa FROM Participacion pa WHERE pa.id.planId = p.id AND pa.id.usuarioId = :userId AND pa.estado = 'confirmado'))")
    List<Plan> findHistorialByUsuario(@Param("hoy") LocalDate hoy, @Param("userId") Long userId);

    @Query("SELECT p FROM Plan p WHERE p.fechaEvento >= :hoy AND " +
           "EXISTS (SELECT pa FROM Participacion pa WHERE pa.id.planId = p.id AND pa.id.usuarioId = :userId)")
    List<Plan> findProximosByUsuario(@Param("hoy") LocalDate hoy, @Param("userId") Long userId);
}