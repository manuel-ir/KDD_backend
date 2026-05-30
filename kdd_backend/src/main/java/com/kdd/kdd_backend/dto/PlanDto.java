package com.kdd.kdd_backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanDto {
    private Long id;
    private String titulo;
    private String descripcion;
    private String categoria;
    private LocalDate fechaEvento;
    private LocalTime horaEvento;
    private String ubicacionTexto;
    private Integer edadMin;
    private Integer edadMax;
    private Integer numMaxPersonas;
    private String idioma;
    private String anfitrionNombre;
    private Long anfitrionId;
    private int numParticipantes;
}
