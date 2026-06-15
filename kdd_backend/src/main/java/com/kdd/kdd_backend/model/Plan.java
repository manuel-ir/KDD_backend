package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "planes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Long id;

    @Column(nullable = false)
    private String titulo;

    private String descripcion;

    @Column(name = "fecha_evento")
    private LocalDate fechaEvento;

    @Column(name = "hora_evento")
    private LocalTime horaEvento;

    @Column(name = "ubicacion_texto")
    private String ubicacionTexto;

    private String iconos;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "edad_min")
    private Integer edadMin;

    @Column(name = "edad_max")
    private Integer edadMax;

    @Column(name = "num_max_personas")
    private Integer numMaxPersonas;

    private String idioma;

    @ManyToOne
    @JoinColumn(name = "id_creador")
    private Usuario creador;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        if (numMaxPersonas == null) numMaxPersonas = 10;
    }
}
