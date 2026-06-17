package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.ConversacionDto;
import com.kdd.kdd_backend.dto.EnviarMensajeDto;
import com.kdd.kdd_backend.dto.MensajeDto;
import com.kdd.kdd_backend.model.Mensaje;
import com.kdd.kdd_backend.model.Usuario;
import com.kdd.kdd_backend.repository.MensajeRepository;
import com.kdd.kdd_backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de mensajeria directa entre usuarios.
 *
 * Permite enviar mensajes, obtener el historial de conversacion
 * entre dos usuarios y borrar toda la conversacion.
 * Los mensajes se almacenan en la tabla mensajes con emisor y receptor.
 */
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


    // Devuelve la lista de conversaciones del usuario: una entrada por cada persona
    // con la que ha intercambiado mensajes, con el ultimo mensaje enviado
    public List<ConversacionDto> getConversaciones(Long userId) {
        List<Long> interlocutores = mensajeRepository.findInterlocutores(userId);
        return interlocutores.stream()
                .map(otroId -> {
                    List<Mensaje> conv = mensajeRepository.findConversacion(userId, otroId);
                    if (conv.isEmpty()) return null;
                    Mensaje ultimo = conv.get(conv.size() - 1);
                    Usuario otro = usuarioRepository.findById(otroId).orElse(null);
                    if (otro == null) return null;
                    String nombre = otro.getNombreUsuario() != null ? otro.getNombreUsuario() : otro.getNombre();
                    return ConversacionDto.builder()
                            .usuarioId(otroId)
                            .nombre(nombre)
                            .fotoPerfil(otro.getFotoPerfil())
                            .ultimoMensaje(ultimo.getContenido())
                            .fechaUltimoMensaje(ultimo.getFechaEnvio() != null ? ultimo.getFechaEnvio().toString() : "")
                            .build();
                })
                .filter(c -> c != null)
                .collect(Collectors.toList());
    }

    @Transactional
    public void borrarConversacion(Long userId, Long otroId) {
        mensajeRepository.deleteConversacion(userId, otroId);
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
