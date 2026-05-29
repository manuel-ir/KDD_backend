package com.kdd.kdd_backend.repository;

import com.kdd.kdd_backend.model.Amistad;
import com.kdd.kdd_backend.model.AmistadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmistadRepository extends JpaRepository<Amistad, AmistadId> {
}