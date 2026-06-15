package com.kdd.kdd_backend.dto;

import lombok.*;

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
