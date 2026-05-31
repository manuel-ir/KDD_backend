package com.kdd.kdd_backend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValoracionDto {
    private Long idValorado;
    private Long idPlan;
    private Integer puntuacion;
    private String comentario;
}
