package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComunidadDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Integer edadMin;
    private Integer edadMax;
    private String adminNombre;
    private Long adminId;
    private int numMiembros;
}
