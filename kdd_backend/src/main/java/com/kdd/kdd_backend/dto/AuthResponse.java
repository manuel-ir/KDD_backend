package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con la respuesta del servidor tras una autenticacion exitosa.
 *
 * Contiene el token JWT propio de la aplicacion que el cliente debe
 * guardar y enviar en la cabecera Authorization de las siguientes peticiones.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String displayName;
    private String email;
}
