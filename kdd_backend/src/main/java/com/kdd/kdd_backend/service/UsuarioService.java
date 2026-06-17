package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.EditarUsuarioDto;
import com.kdd.kdd_backend.dto.UsuarioDto;
import com.kdd.kdd_backend.model.Usuario;
import com.kdd.kdd_backend.repository.UsuarioRepository;
import com.kdd.kdd_backend.repository.ValoracionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

/**
 * Servicio con la logica de negocio relacionada con los usuarios.
 *
 * Gestiona la consulta del perfil propio, la edicion de datos personales
 * (nombre de usuario, descripcion, fecha de nacimiento, foto) y el
 * control del limite de cambios de alias (maximo 3 cambios permitidos).
 */
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ValoracionRepository valoracionRepository;

    public UsuarioDto getPerfil(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return toDto(usuario);
    }

    public UsuarioDto editarPerfil(Long userId, EditarUsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getNombreUsuario() != null) {
            String nuevoAlias = dto.getNombreUsuario().isBlank() ? null : dto.getNombreUsuario();
            boolean cambiaAlias = nuevoAlias != null &&
                    usuario.getNombreUsuario() != null &&
                    !nuevoAlias.equals(usuario.getNombreUsuario());
            if (cambiaAlias) {
                if (usuario.getContadorCambiosAlias() >= 3) {
                    throw new RuntimeException("Has alcanzado el límite de 3 cambios de alias");
                }
                usuario.setContadorCambiosAlias(usuario.getContadorCambiosAlias() + 1);
            }
            usuario.setNombreUsuario(nuevoAlias);
        }
        if (dto.getDescripcion() != null) usuario.setDescripcion(dto.getDescripcion());
        if (dto.getFotoPerfil() != null) usuario.setFotoPerfil(dto.getFotoPerfil());
        if (dto.getFechaNacimiento() != null && !dto.getFechaNacimiento().isBlank()) {
            usuario.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
        }

        usuarioRepository.save(usuario);
        return toDto(usuario);
    }

    public UsuarioDto toDto(Usuario u) {
        Integer edad = null;
        String fechaNacimientoStr = null;
        if (u.getFechaNacimiento() != null) {
            edad = Period.between(u.getFechaNacimiento(), LocalDate.now()).getYears();
            fechaNacimientoStr = u.getFechaNacimiento().toString();
        }
        Double media = valoracionRepository.findMediaPuntuacionByIdValorado(u.getId());
        return UsuarioDto.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .nombreUsuario(u.getNombreUsuario())
                .email(u.getEmail())
                .fotoPerfil(u.getFotoPerfil())
                .descripcion(u.getDescripcion())
                .edad(edad)
                .fechaNacimiento(fechaNacimientoStr)
                .puntuacionMedia(media)
                .esGoogleUser("google".equals(u.getProveedor()))
                .contadorCambiosAlias(u.getContadorCambiosAlias())
                .build();
    }
}