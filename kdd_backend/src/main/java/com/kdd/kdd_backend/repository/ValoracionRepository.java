package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Valoracion;
import com.kdd.kdd_backend.model.ValoracionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Valoracion.
 * Incluye una consulta para calcular la puntuacion media de un usuario.
 */
@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, ValoracionId> {

    @Query("SELECT AVG(v.puntuacion) FROM Valoracion v WHERE v.idValorado = :userId")
    Double findMediaPuntuacionByIdValorado(@Param("userId") Long userId);
}