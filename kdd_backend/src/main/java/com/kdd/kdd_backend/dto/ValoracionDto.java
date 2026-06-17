package com.kdd.kdd_backend.dto;

import lombok.*;

/**
 * DTO con los datos de una valoracion enviada por el cliente.
 * Contiene el id del usuario valorado, el id del plan y la puntuacion (1-5).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionDto {
    private Long idValorado;
    private Long idPlan;
    private Integer puntuacion;
    private String comentario;
}
