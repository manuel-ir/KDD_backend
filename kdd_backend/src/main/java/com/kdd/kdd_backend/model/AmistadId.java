package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

/**
 * Clave primaria compuesta para la entidad Amistad.
 * Agrupa los ids de los dos usuarios que forman la relacion.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AmistadId implements Serializable {
    private Long usuarioId;
    private Long amigoId;
}
