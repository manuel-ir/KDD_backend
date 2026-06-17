package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.ConversacionDto;
import com.kdd.kdd_backend.dto.EnviarMensajeDto;
import com.kdd.kdd_backend.dto.MensajeDto;
import com.kdd.kdd_backend.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de mensajes entre usuarios.
 *
 * Permite enviar mensajes directos entre amigos, consultar conversaciones
 * y borrar el historial de chat con otro usuario.
 *
 * Todos los endpoints requieren JWT valido.
 */
@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;

    // Lista todas las conversaciones del usuario (independientemente de amistad)
    @GetMapping("/conversaciones")
    public ResponseEntity<List<ConversacionDto>> getConversaciones(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(mensajeService.getConversaciones(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<MensajeDto>> getConversacion(Authentication auth,
                                                             @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(mensajeService.getConversacion(userId, id));
    }

    @PostMapping("/{id}")
    public ResponseEntity<MensajeDto> enviar(Authentication auth,
                                              @PathVariable Long id,
                                              @RequestBody EnviarMensajeDto dto) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(mensajeService.enviar(userId, id, dto));
    }

    @DeleteMapping("/conversacion/{id}")
    public ResponseEntity<Void> borrarConversacion(Authentication auth,
                                                    @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        mensajeService.borrarConversacion(userId, id);
        return ResponseEntity.ok().build();
    }
}
