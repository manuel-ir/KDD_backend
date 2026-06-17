package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con el contenido de un mensaje que el cliente quiere enviar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarMensajeDto {
    private String contenido;
}
