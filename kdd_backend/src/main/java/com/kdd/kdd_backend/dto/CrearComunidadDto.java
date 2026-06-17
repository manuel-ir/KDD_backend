package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos para crear una nueva comunidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CrearComunidadDto {
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private Integer edadMin;
    private Integer edadMax;
    private String fotoComunidadUrl;
    private String categoria;
}
