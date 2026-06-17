package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.PertenenciaComunidad;
import com.kdd.kdd_backend.model.PertenenciaComunidadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad PertenenciaComunidad.
 * Permite buscar y contar los miembros de una comunidad.
 */
@Repository
public interface PertenenciaComunidadRepository extends JpaRepository<PertenenciaComunidad, PertenenciaComunidadId> {
    int countByIdComunidadId(Long comunidadId);
    boolean existsByIdUsuarioIdAndIdComunidadId(Long usuarioId, Long comunidadId);
    void deleteByIdUsuarioIdAndIdComunidadId(Long usuarioId, Long comunidadId);
    List<PertenenciaComunidad> findByIdComunidadId(Long comunidadId);
    List<PertenenciaComunidad> findByIdUsuarioId(Long usuarioId);
}
