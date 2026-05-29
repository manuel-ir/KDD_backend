package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pertenencias_plan_comunidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PertenenciaPlanComunidad {

    @EmbeddedId
    private PertenenciaPlanComunidadId id;

    @ManyToOne
    @MapsId("planId")
    @JoinColumn(name = "id_plan")
    private Plan plan;

    @ManyToOne
    @MapsId("comunidadId")
    @JoinColumn(name = "id_comunidad")
    private Comunidad comunidad;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion;

    @PrePersist
    protected void onCreate() {
        fechaUnion = LocalDateTime.now();
        if (estado == null) estado = "pendiente";
    }
}
