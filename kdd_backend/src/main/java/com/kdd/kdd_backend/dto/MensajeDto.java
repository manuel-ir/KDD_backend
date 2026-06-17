package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos de un mensaje para enviar al cliente.
 * Incluye los ids de emisor y receptor, el contenido y la fecha de envio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeDto {
    private Long id;
    private Long emisorId;
    private Long receptorId;
    private String contenido;
    private String fechaEnvio; // ISO format: "2024-06-12T10:30:00"
}
