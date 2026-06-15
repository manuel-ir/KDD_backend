package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.EnviarMensajeDto;
import com.kdd.kdd_backend.dto.MensajeDto;
import com.kdd.kdd_backend.model.Mensaje;
import com.kdd.kdd_backend.repository.MensajeRepository;
import com.kdd.kdd_backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MensajeService {

    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;

    public List<MensajeDto> getConversacion(Long userId, Long otroId) {
        return mensajeRepository.findConversacion(userId, otroId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MensajeDto enviar(Long emisorId, Long receptorId, EnviarMensajeDto dto) {
        if (dto.getContenido() == null || dto.getContenido().isBlank()) {
            throw new RuntimeException("El mensaje no puede estar vacío");
        }

        usuarioRepository.findById(receptorId)
                .orElseThrow(() -> new RuntimeException("Usuario destinatario no encontrado"));

        Mensaje mensaje = Mensaje.builder()
                .contenido(dto.getContenido())
                .emisor(usuarioRepository.getReferenceById(emisorId))
                .receptor(usuarioRepository.getReferenceById(receptorId))
                .build();

        return toDto(mensajeRepository.save(mensaje));
    }

    private MensajeDto toDto(Mensaje m) {
        return MensajeDto.builder()
                .id(m.getId())
                .emisorId(m.getEmisor().getId())
                .receptorId(m.getReceptor().getId())
                .contenido(m.getContenido())
                .fechaEnvio(m.getFechaEnvio() != null ? m.getFechaEnvio().toString() : null)
                .build();
    }
}
