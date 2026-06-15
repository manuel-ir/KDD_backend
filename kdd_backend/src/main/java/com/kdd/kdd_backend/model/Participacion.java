package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "participaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participacion {

    @EmbeddedId
    private ParticipacionId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("planId")
    @JoinColumn(name = "id_plan")
    private Plan plan;

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
