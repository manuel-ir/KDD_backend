package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ValoracionId implements Serializable {
    private Long idValorador;
    private Long idValorado;
    private Long idPlan;
}
