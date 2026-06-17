package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * Objeto de transferencia de datos (DTO) para la peticion de autenticacion con Google.
 *
 * El cliente Android envia este objeto en el body del POST /api/auth/google.
 * El idToken es el token generado por Google Sign-In en el dispositivo movil.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoogleAuthRequest {
    private String idToken;
}
