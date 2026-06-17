package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.PertenenciaPlanComunidad;
import com.kdd.kdd_backend.model.PertenenciaPlanComunidadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad PertenenciaPlanComunidad.
 * Permite buscar los planes asociados a una comunidad concreta.
 */
@Repository
public interface PertenenciaPlanComunidadRepository extends JpaRepository<PertenenciaPlanComunidad, PertenenciaPlanComunidadId> {
    List<PertenenciaPlanComunidad> findByIdComunidadId(Long comunidadId);
    boolean existsByIdPlanIdAndIdComunidadId(Long planId, Long comunidadId);
}
