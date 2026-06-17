package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos de una conversacion reciente para mostrar en la lista de chats.
 * Incluye el id y nombre del otro usuario y el ultimo mensaje intercambiado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversacionDto {
    private Long usuarioId;
    private String nombre;
    private String fotoPerfil;
    private String ultimoMensaje;
    private String fechaUltimoMensaje;
}
