package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.ComunidadDto;
import com.kdd.kdd_backend.dto.CrearComunidadDto;
import com.kdd.kdd_backend.service.ComunidadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunidades")
@RequiredArgsConstructor
public class ComunidadController {

    private final ComunidadService comunidadService;

    @GetMapping
    public ResponseEntity<List<ComunidadDto>> listar() {
        return ResponseEntity.ok(comunidadService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComunidadDto> detalle(@PathVariable Long id) {
        return ResponseEntity.ok(comunidadService.getDetalle(id));
    }

    @PostMapping
    public ResponseEntity<ComunidadDto> crear(Authentication auth,
                                               @RequestBody CrearComunidadDto dto) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(comunidadService.crear(userId, dto));
    }

    @PostMapping("/{id}/unirse")
    public ResponseEntity<Void> unirse(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        comunidadService.unirse(userId, id);
        return ResponseEntity.ok().build();
    }
}
