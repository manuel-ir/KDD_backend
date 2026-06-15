package com.kdd.kdd_backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDto {
    private Long id;
    private String nombre;
    private String email;
    private String fotoPerfil;
    private LocalDateTime fechaRegistro;
    private Boolean esInvitado;
}