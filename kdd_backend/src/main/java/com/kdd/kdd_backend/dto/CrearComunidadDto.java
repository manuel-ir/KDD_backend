package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearComunidadDto {
    private String nombre;
    private String descripcion;
    private Integer edadMin;
    private Integer edadMax;
}
