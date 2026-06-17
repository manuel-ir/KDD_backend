package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Usuario.
 * Spring Data genera automaticamente las implementaciones de los metodos
 * a partir de su nombre o de las consultas JPQL anotadas con @Query.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByGoogleId(String googleId);
    boolean existsByGoogleId(String googleId);
    Optional<Usuario> findByEmail(String email);
}
