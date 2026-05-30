package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "valoraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(ValoracionId.class)
public class Valoracion {

    // Clave primaria compuesta (valorador + valorado + plan)
    @Id
    @Column(name = "id_valorador")
    private Long idValorador;

    @Id
    @Column(name = "id_valorado")
    private Long idValorado;

    @Id
    @Column(name = "id_plan")
    private Long idPlan;

    @ManyToOne
    @JoinColumn(name = "id_valorador", insertable = false, updatable = false)
    private Usuario valorador;

    @ManyToOne
    @JoinColumn(name = "id_valorado", insertable = false, updatable = false)
    private Usuario valorado;

    @ManyToOne
    @JoinColumn(name = "id_plan", insertable = false, updatable = false)
    private Plan plan;

    @Column(nullable = false)
    private Integer puntuacion;

    private String comentario;
}