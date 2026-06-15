package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Participacion;
import com.kdd.kdd_backend.model.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {
}