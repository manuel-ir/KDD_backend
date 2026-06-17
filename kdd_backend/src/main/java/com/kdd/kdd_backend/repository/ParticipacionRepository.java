package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Participacion;
import com.kdd.kdd_backend.model.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Participacion.
 *
 * Incluye consultas personalizadas para contar participantes, sumar
 * acompanantes y verificar si un usuario esta apuntado con un estado concreto.
 */
@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {
    int countByIdPlanId(Long planId);

    @Query("SELECT COALESCE(SUM(p.acompanantes), 0) FROM Participacion p WHERE p.id.planId = :planId")
    int sumAcompanantesByPlanId(@Param("planId") Long planId);
    boolean existsByIdUsuarioIdAndIdPlanId(Long usuarioId, Long planId);
    List<Participacion> findByIdUsuarioId(Long usuarioId);
    List<Participacion> findByIdPlanId(Long planId);
    List<Participacion> findByIdPlanIdAndEstado(Long planId, String estado);
    boolean existsByIdUsuarioIdAndIdPlanIdAndEstado(Long usuarioId, Long planId, String estado);
    boolean existsByIdUsuarioIdAndIdPlanIdAndPresenteTrue(Long usuarioId, Long planId);
    boolean existsByIdPlanIdAndPresenteTrue(Long planId);
    int countByIdPlanIdAndEstado(Long planId, String estado);
    void deleteByIdUsuarioIdAndIdPlanId(Long usuarioId, Long planId);
    void deleteByIdPlanId(Long planId);
}