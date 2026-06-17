package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

/**
 * Clave primaria compuesta para PertenenciaComunidad.
 * Agrupa los ids de usuario y comunidad.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PertenenciaComunidadId implements Serializable {
    private Long usuarioId;
    private Long comunidadId;
}
