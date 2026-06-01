package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.AmistadDto;
import com.kdd.kdd_backend.model.Amistad;
import com.kdd.kdd_backend.model.AmistadId;
import com.kdd.kdd_backend.model.Usuario;
import com.kdd.kdd_backend.repository.AmistadRepository;
import com.kdd.kdd_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AmistadService {

    private final AmistadRepository amistadRepository;
    private final UsuarioRepository usuarioRepository;

    public List<AmistadDto> listarAmigos(Long userId) {
        return amistadRepository.findAmigosConfirmados(userId).stream()
                .map(a -> toDto(a, userId))
                .collect(Collectors.toList());
    }

    public List<AmistadDto> listarSolicitudes(Long userId) {
        return amistadRepository.findSolicitudesPendientes(userId).stream()
                .map(a -> toDto(a, userId))
                .collect(Collectors.toList());
    }

    public void enviarSolicitud(Long solicitanteId, Long destinatarioId) {
        if (solicitanteId.equals(destinatarioId)) {
            throw new RuntimeException("No puedes añadirte a ti mismo");
        }

        amistadRepository.findRelacion(solicitanteId, destinatarioId).ifPresent(a -> {
            throw new RuntimeException("Ya existe una relación con este usuario");
        });

        usuarioRepository.findById(destinatarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Amistad amistad = new Amistad();
        amistad.setId(new AmistadId(solicitanteId, destinatarioId));
        amistad.setUsuario(usuarioRepository.getReferenceById(solicitanteId));
        amistad.setAmigo(usuarioRepository.getReferenceById(destinatarioId));
        amistad.setEstado("pendiente");
        amistadRepository.save(amistad);
    }

    public void aceptarSolicitud(Long userId, Long solicitanteId) {
        Amistad amistad = amistadRepository.findRelacion(solicitanteId, userId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if (!amistad.getEstado().equals("pendiente")) {
            throw new RuntimeException("La solicitud no está pendiente");
        }

        amistad.setEstado("confirmado");
        amistad.setFechaConfirmacion(LocalDateTime.now());
        amistadRepository.save(amistad);
    }

    public void eliminarAmistad(Long userId, Long otroId) {
        Amistad amistad = amistadRepository.findRelacion(userId, otroId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));
        amistadRepository.delete(amistad);
    }

    private AmistadDto toDto(Amistad a, Long userId) {
        Usuario otro = a.getId().getUsuarioId().equals(userId) ? a.getAmigo() : a.getUsuario();
        return AmistadDto.builder()
                .idAmigo(otro.getId())
                .nombre(otro.getNombre())
                .fotoPerfil(otro.getFotoPerfil())
                .estado(a.getEstado())
                .build();
    }
}
