package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Amistad;
import com.kdd.kdd_backend.model.AmistadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmistadRepository extends JpaRepository<Amistad, AmistadId> {

    @Query("SELECT a FROM Amistad a WHERE (a.id.usuarioId = :uid OR a.id.amigoId = :uid) AND a.estado = 'confirmado'")
    List<Amistad> findAmigosConfirmados(@Param("uid") Long uid);

    @Query("SELECT a FROM Amistad a WHERE (a.id.usuarioId = :uid OR a.id.amigoId = :uid) AND a.estado = 'pendiente'")
    List<Amistad> findSolicitudesPendientes(@Param("uid") Long uid);

    @Query("SELECT a FROM Amistad a WHERE (a.id.usuarioId = :u1 AND a.id.amigoId = :u2) OR (a.id.usuarioId = :u2 AND a.id.amigoId = :u1)")
    Optional<Amistad> findRelacion(@Param("u1") Long u1, @Param("u2") Long u2);

    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Amistad a WHERE ((a.id.usuarioId = :u1 AND a.id.amigoId = :u2) OR (a.id.usuarioId = :u2 AND a.id.amigoId = :u1)) AND a.estado = 'confirmado'")
    boolean sonAmigos(@Param("u1") Long u1, @Param("u2") Long u2);
}