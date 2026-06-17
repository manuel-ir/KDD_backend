package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos de un participante de un plan.
 * Incluye si ha confirmado su presencia (presente) y cuantos acompanantes lleva.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipanteDto {
    private Long id;
    private String nombre;
    private String nombreUsuario;
    private Integer edad;
    private String descripcion;
    private String fotoPerfil;
    private boolean presente;
    private Double puntuacion;
    private Integer acompanantes;
}
