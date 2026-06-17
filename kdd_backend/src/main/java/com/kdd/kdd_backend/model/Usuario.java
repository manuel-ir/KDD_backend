package com.kdd.kdd_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa a un usuario registrado en la aplicacion.
 *
 * Mapea la tabla "usuarios" de la base de datos. Un usuario puede
 * autenticarse con Google (campo googleId) o con email y contrasena.
 * El campo proveedor indica el metodo de registro ("google" o "email").
 *
 * El alias (nombreUsuario) es opcional pero unico, y solo puede cambiarse
 * un maximo de 3 veces a lo largo de la vida de la cuenta.
 */
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

    @Column(name = "proveedor")
    private String proveedor; // "google" o "email"

    @Column(name = "contador_cambios_alias", nullable = false)
    private int contadorCambiosAlias = 0;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
        if (esInvitado == null) esInvitado = false;
    }
}
