package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

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

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "edad_min")
    private Integer edadMin;

    @Column(name = "edad_max")
    private Integer edadMax;

    @ManyToOne
    @JoinColumn(name = "id_admin")
    private Usuario admin;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
    }
}
