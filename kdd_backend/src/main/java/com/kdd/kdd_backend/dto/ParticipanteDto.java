package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipanteDto {
    private Long id;
    private String nombre;
    private Integer edad;
    private String descripcion;
    private String fotoPerfil;
}
