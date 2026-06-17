package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos necesarios para crear o editar un plan.
 * El campo horaHasta es opcional: si se especifica, el plan dejara de
 * aparecer en Explora cuando esa hora pase en la fecha del evento.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPlanDto {
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
    private Double latitud;
    private Double longitud;
    private String fotoPlanUrl;
    private Long comunidadId;
    private Integer acompanantes;
}
