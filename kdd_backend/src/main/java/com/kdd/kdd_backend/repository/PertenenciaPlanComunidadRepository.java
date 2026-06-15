package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.PertenenciaPlanComunidad;
import com.kdd.kdd_backend.model.PertenenciaPlanComunidadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PertenenciaPlanComunidadRepository extends JpaRepository<PertenenciaPlanComunidad, PertenenciaPlanComunidadId> {
    List<PertenenciaPlanComunidad> findByIdComunidadId(Long comunidadId);
    boolean existsByIdPlanIdAndIdComunidadId(Long planId, Long comunidadId);
    void deleteByIdPlanId(Long planId);
}
