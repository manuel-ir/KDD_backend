package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos basicos de un miembro de una comunidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MiembroComunidadDto {
    private Long id;
    private String nombre;
    private String fotoPerfil;
    private Integer edad;
}
