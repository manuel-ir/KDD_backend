package com.kdd.kdd_backend.dto;

import lombok.Data;

/**
 * DTO para el registro de un usuario con email y contrasena.
 * Se recibe en POST /api/auth/registro.
 */
@Data
public class RegistroEmailDto {
    private String nombre;
    private String email;
    private String password;
}
