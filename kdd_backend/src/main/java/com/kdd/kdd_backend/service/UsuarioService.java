package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.EditarUsuarioDto;
import com.kdd.kdd_backend.dto.UsuarioDto;
import com.kdd.kdd_backend.model.Usuario;
import com.kdd.kdd_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDto getPerfil(Long userId) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return toDto(usuario);
    }

    public UsuarioDto editarPerfil(Long userId, EditarUsuarioDto dto) {
        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (dto.getNombre() != null) usuario.setNombre(dto.getNombre());
        if (dto.getDescripcion() != null) usuario.setDescripcion(dto.getDescripcion());
        if (dto.getEdad() != null) usuario.setEdad(dto.getEdad());
        if (dto.getFotoPerfil() != null) usuario.setFotoPerfil(dto.getFotoPerfil());

        usuarioRepository.save(usuario);
        return toDto(usuario);
    }

    private UsuarioDto toDto(Usuario u) {
        return UsuarioDto.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .email(u.getEmail())
                .fotoPerfil(u.getFotoPerfil())
                .descripcion(u.getDescripcion())
                .edad(u.getEdad())
                .build();
    }
}
