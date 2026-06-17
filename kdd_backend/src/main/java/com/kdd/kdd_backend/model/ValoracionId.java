package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

/**
 * Clave primaria ternaria para la entidad Valoracion.
 * Agrupa los ids del valorador, el valorado y el plan en el que se realiza la valoracion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ValoracionId implements Serializable {
    private Long idValorador;
    private Long idValorado;
    private Long idPlan;
}
