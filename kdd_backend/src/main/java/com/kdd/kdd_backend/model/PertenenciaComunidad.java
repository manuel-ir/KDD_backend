package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa la pertenencia de un usuario a una comunidad.
 *
 * Mapea la tabla "pertenencias_comunidad" (relacion N:M entre Usuario y Comunidad).
 * La clave primaria es compuesta (id_usuario, id_comunidad).
 * El campo "estado" puede ser "pendiente" o "confirmado".
 */
@Entity
@Table(name = "pertenencias_comunidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PertenenciaComunidad {

    @EmbeddedId
    private PertenenciaComunidadId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("comunidadId")
    @JoinColumn(name = "id_comunidad")
    private Comunidad comunidad;

    @Column(nullable = false)
    private String estado;

    @Column(name = "mensaje_solicitud", columnDefinition = "TEXT")
    private String mensajeSolicitud;

    @Column(name = "fecha_union")
    private LocalDateTime fechaUnion;

    @PrePersist
    protected void onCreate() {
        fechaUnion = LocalDateTime.now();
        if (estado == null) estado = "pendiente";
    }
}
