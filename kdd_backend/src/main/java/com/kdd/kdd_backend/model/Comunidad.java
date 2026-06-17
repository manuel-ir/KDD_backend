package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una comunidad de usuarios.
 *
 * Mapea la tabla "comunidades". Una comunidad tiene un administrador
 * (el usuario que la creo) y puede tener restriccion de edad.
 * Los planes pueden asociarse a una comunidad.
 */
@Entity
@Table(name = "comunidades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comunidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comunidad")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String descripcion;

    private String ubicacion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "edad_min")
    private Integer edadMin;

    @Column(name = "edad_max")
    private Integer edadMax;

    @Column(name = "foto_comunidad_url")
    private String fotoComunidadUrl;

    private String categoria;

    @ManyToOne
    @JoinColumn(name = "id_admin")
    private Usuario admin;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
