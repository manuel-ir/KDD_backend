package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Valoracion;
import com.kdd.kdd_backend.model.ValoracionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValoracionRepository extends JpaRepository<Valoracion, ValoracionId> {
}