package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PertenenciaPlanComunidadId implements Serializable {
    private Long planId;
    private Long comunidadId;
}
