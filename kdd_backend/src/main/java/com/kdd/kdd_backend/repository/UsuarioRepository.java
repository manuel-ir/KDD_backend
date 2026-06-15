package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByGoogleId(String googleId);
    boolean existsByGoogleId(String googleId);
    Optional<Usuario> findByEmail(String email);
}
