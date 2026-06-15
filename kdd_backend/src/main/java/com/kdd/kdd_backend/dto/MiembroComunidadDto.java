package com.kdd.kdd_backend.dto;

import lombok.*;

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
