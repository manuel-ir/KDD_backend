package com.kdd.kdd_backend.dto;

import lombok.Data;

/**
 * DTO para el inicio de sesion con email y contrasena.
 * Se recibe en POST /api/auth/login-email.
 */
@Data
public class LoginEmailDto {
    private String email;
    private String password;
}
