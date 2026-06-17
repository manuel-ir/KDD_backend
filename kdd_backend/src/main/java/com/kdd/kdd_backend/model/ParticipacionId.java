package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

/**
 * Clave primaria compuesta para la entidad Participacion.
 *
 * En JPA, cuando la clave primaria de una tabla tiene mas de una columna,
 * se usa una clase separada anotada con @Embeddable.
 * Esta clase agrupa los ids de usuario y plan.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ParticipacionId implements Serializable {
    private Long usuarioId;
    private Long planId;
}
