package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO que representa los datos del perfil de un usuario para enviar al cliente.
 *
 * No incluye datos sensibles como la contrasena o el googleId.
 * El campo esGoogleUser indica si el usuario inicio sesion con Google.
 * El contadorCambiosAlias indica cuantas veces ha cambiado su alias (maximo 3).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String nombreUsuario;
    private String email;
    private String fotoPerfil;
    private String descripcion;
    private Integer edad;
    private String fechaNacimiento; // ISO YYYY-MM-DD (solo visible para el propio usuario)
    private Double puntuacionMedia;
    private boolean esGoogleUser;
    private int contadorCambiosAlias;
}
