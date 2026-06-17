package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad Mensaje.
 * Incluye consultas para obtener y eliminar conversaciones entre dos usuarios.
 */
@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m WHERE (m.emisor.id = :u1 AND m.receptor.id = :u2) OR (m.emisor.id = :u2 AND m.receptor.id = :u1) ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacion(@Param("u1") Long u1, @Param("u2") Long u2);

    @Query("SELECT DISTINCT CASE WHEN m.emisor.id = :uid THEN m.receptor.id ELSE m.emisor.id END FROM Mensaje m WHERE m.emisor.id = :uid OR m.receptor.id = :uid")
    List<Long> findInterlocutores(@Param("uid") Long uid);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Mensaje m WHERE (m.emisor.id = :u1 AND m.receptor.id = :u2) OR (m.emisor.id = :u2 AND m.receptor.id = :u1)")
    void deleteConversacion(@Param("u1") Long u1, @Param("u2") Long u2);
}