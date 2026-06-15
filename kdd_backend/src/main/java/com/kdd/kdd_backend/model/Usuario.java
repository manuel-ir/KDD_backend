package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(name = "foto_perfil")
    private String fotoPerfil;

    @Column(name = "google_id", unique = true)
    private String googleId;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "es_invitado")
    private Boolean esInvitado;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "nombre_usuario", unique = true)
    private String nombreUsuario;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (esInvitado == null) esInvitado = false;
    }
}
