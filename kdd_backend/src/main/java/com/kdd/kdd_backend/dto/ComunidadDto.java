package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos de una comunidad para enviar al cliente.
 * Incluye si el usuario autenticado es miembro o administrador.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComunidadDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private Integer edadMin;
    private Integer edadMax;
    private String adminNombre;
    private Long adminId;
    private int numMiembros;
    private boolean miembro;
    private boolean admin;
    private String fotoComunidadUrl;
    private String categoria;
}
