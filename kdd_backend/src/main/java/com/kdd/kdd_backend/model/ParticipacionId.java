package com.kdd.kdd_backend.model;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ParticipacionId implements Serializable {
    private Long usuarioId;
    private Long planId;
}
