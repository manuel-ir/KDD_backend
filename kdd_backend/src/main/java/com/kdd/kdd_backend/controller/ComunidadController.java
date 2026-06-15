package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.ComunidadDto;
import com.kdd.kdd_backend.dto.CrearComunidadDto;
import com.kdd.kdd_backend.dto.MiembroComunidadDto;
import com.kdd.kdd_backend.dto.PlanDto;
import com.kdd.kdd_backend.service.ComunidadService;
import com.kdd.kdd_backend.service.PlanService;
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
    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<ComunidadDto>> listar() {
        return ResponseEntity.ok(comunidadService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComunidadDto> detalle(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(comunidadService.getDetalle(id, userId));
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

    @DeleteMapping("/{id}/abandonar")
    public ResponseEntity<Void> abandonar(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        comunidadService.abandonar(userId, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/miembros")
    public ResponseEntity<List<MiembroComunidadDto>> miembros(@PathVariable Long id) {
        return ResponseEntity.ok(comunidadService.getMiembros(id));
    }

    @GetMapping("/{id}/planes")
    public ResponseEntity<List<PlanDto>> planes(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.getPlanesComunidad(id, userId));
    }
}
