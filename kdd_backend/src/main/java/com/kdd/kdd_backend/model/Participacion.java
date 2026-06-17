package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa la participacion de un usuario en un plan.
 *
 * Mapea la tabla "participaciones". La clave primaria es compuesta:
 * (id_usuario, id_plan), implementada en ParticipacionId.
 *
 * El campo "presente" se activa cuando el usuario confirma que asistio
 * al plan (despues de que este haya comenzado). Solo los usuarios con
 * presente=true pueden valorar y ser valorados por los demas participantes.
 *
 * El campo "acompanantes" es informativo: indica cuantas personas lleva
 * consigo el participante, pero no afecta al aforo maximo del plan.
 */
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

    @Column(name = "presente", nullable = false)
    private boolean presente = false;

    @Column(name = "acompanantes", nullable = false)
    private int acompanantes = 1;

    @PrePersist
    protected void onCreate() {
        fechaUnion = LocalDateTime.now();
        if (estado == null) estado = "confirmado";
    }
}
