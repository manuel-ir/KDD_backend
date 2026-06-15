package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AmistadDto {
    private Long idAmigo;
    private String nombre;
    private String fotoPerfil;
    private String estado;
}
