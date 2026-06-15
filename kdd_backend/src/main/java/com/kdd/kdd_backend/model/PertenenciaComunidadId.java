package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PertenenciaComunidadId implements Serializable {
    private Long usuarioId;
    private Long comunidadId;
}
