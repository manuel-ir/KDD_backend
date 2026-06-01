package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Participacion;
import com.kdd.kdd_backend.model.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {
    int countByIdPlanId(Long planId);
    boolean existsByIdUsuarioIdAndIdPlanId(Long usuarioId, Long planId);
    List<Participacion> findByIdUsuarioId(Long usuarioId);
    void deleteByIdUsuarioIdAndIdPlanId(Long usuarioId, Long planId);
}