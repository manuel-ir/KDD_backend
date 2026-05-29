package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarUsuarioDto {
    private String nombre;
    private String descripcion;
    private Integer edad;
    private String fotoPerfil;
}
