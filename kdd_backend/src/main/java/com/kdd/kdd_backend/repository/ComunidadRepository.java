package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Comunidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComunidadRepository extends JpaRepository<Comunidad, Long> {
}