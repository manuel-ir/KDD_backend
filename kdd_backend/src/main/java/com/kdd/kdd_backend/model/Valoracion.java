package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad que representa la valoracion de un usuario a otro en un plan concreto.
 *
 * Mapea la tabla "valoraciones". La clave primaria es ternaria:
 * (id_valorador, id_valorado, id_plan), implementada en ValoracionId.
 *
 * Un usuario solo puede valorar a otro una vez por plan,
 * y ambos deben haber confirmado su presencia (campo presente=true).
 */
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