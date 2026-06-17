package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos de un plan para enviar al cliente.
 *
 * Incluye informacion calculada dinamicamente: si el usuario autenticado
 * es miembro del plan (miembro), si es el creador (creador), el numero
 * de participantes y el aforo maximo.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private String fechaEvento;
    private String horaEvento;
    private String horaHasta;
    private String ubicacionTexto;
    private Integer edadMin;
    private Integer edadMax;
    private Integer numMaxPersonas;
    private String idioma;
    private String anfitrionNombre;
    private Long anfitrionId;
    private Double latitud;
    private Double longitud;
    private String fotoPlanUrl;
    private int numParticipantes;
    private int numApuntados;
    private boolean miembro;
    private boolean creador;
    private boolean pendiente;
}
