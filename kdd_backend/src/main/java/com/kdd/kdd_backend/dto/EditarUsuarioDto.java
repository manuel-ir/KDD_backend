package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los campos que el usuario puede modificar en su perfil.
 * Los campos nulos se ignoran: solo se actualizan los que vienen informados.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditarUsuarioDto {
    private String nombre;
    private String nombreUsuario;
    private String descripcion;
    private String fechaNacimiento; // ISO YYYY-MM-DD
    private String fotoPerfil;
}
