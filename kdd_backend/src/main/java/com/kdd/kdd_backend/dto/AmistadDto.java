package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO que representa una relacion de amistad en la respuesta al cliente.
 * Incluye el id y nombre del otro usuario y el estado de la relacion.
 */
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
