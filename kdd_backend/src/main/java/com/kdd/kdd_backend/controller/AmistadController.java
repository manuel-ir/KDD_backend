package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.AmistadDto;
import com.kdd.kdd_backend.service.AmistadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de relaciones de amistad entre usuarios.
 *
 * Gestiona el ciclo completo de una amistad: enviar solicitud,
 * aceptarla, rechazarla o eliminar la relacion existente.
 * Tambien permite consultar la lista de amigos y solicitudes pendientes.
 *
 * Todos los endpoints requieren JWT valido.
 */
@RestController
@RequestMapping("/api/amistades")
@RequiredArgsConstructor
public class AmistadController {

    private final AmistadService amistadService;

    @GetMapping
    public ResponseEntity<List<AmistadDto>> listarAmigos(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(amistadService.listarAmigos(userId));
    }

    @GetMapping("/solicitudes")
    public ResponseEntity<List<AmistadDto>> listarSolicitudes(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(amistadService.listarSolicitudes(userId));
    }

    @GetMapping("/enviadas")
    public ResponseEntity<List<AmistadDto>> listarEnviadas(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(amistadService.listarEnviadas(userId));
    }

    @PostMapping("/{id}")
    public ResponseEntity<Void> enviarSolicitud(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        amistadService.enviarSolicitud(userId, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/aceptar")
    public ResponseEntity<Void> aceptar(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        amistadService.aceptarSolicitud(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        amistadService.eliminarAmistad(userId, id);
        return ResponseEntity.ok().build();
    }
}
