package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.PertenenciaComunidad;
import com.kdd.kdd_backend.model.PertenenciaComunidadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PertenenciaComunidadRepository extends JpaRepository<PertenenciaComunidad, PertenenciaComunidadId> {
}