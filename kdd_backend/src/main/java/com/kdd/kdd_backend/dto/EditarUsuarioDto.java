package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarUsuarioDto {
    private String nombre;
    private String nombreUsuario;
    private String descripcion;
    private String fechaNacimiento;
    private String fotoPerfil;
}
