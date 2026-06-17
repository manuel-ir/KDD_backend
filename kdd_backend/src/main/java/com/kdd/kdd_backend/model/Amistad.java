package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa la relacion de amistad entre dos usuarios.
 *
 * Mapea la tabla "amistades". La relacion es unidireccional en la base
 * de datos (usuario -> amigo), pero la logica del servicio busca
 * en ambas direcciones para gestionar la amistad bidireccionalmente.
 *
 * Estados posibles: "pendiente" (solicitud enviada) o "confirmado" (aceptada).
 */
@Entity
@Table(name = "amistades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Amistad {

    @EmbeddedId
    private AmistadId id;

    @ManyToOne
    @MapsId("usuarioId")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @MapsId("amigoId")
    @JoinColumn(name = "id_amigo")
    private Usuario amigo;

    @Column(nullable = false)
    private String estado;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_confirmacion")
    private LocalDateTime fechaConfirmacion;

    @PrePersist
    protected void onCreate() {
        fechaSolicitud = LocalDateTime.now();
        if (estado == null) estado = "pendiente";
    }
}
