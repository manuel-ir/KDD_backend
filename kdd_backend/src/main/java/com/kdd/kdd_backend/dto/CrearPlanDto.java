package com.kdd.kdd_backend.dto;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearPlanDto {
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
    private Double latitud;
    private Double longitud;
    private Long comunidadId;
}
