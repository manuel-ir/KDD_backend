package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.CrearPlanDto;
import com.kdd.kdd_backend.dto.PlanDto;
import com.kdd.kdd_backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @GetMapping
    public ResponseEntity<List<PlanDto>> listar() {
        return ResponseEntity.ok(planService.listarPlanes());
    }

    @GetMapping("/mis-planes")
    public ResponseEntity<List<PlanDto>> misPlanes(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.misPlanes(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> detalle(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.getDetalle(id, userId));
    }

    @PostMapping
    public ResponseEntity<PlanDto> crear(Authentication auth,
                                          @RequestBody CrearPlanDto dto) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.crearPlan(userId, dto));
    }

    @PostMapping("/{id}/unirse")
    public ResponseEntity<Void> unirse(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        planService.unirse(userId, id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/abandonar")
    public ResponseEntity<Void> abandonar(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        planService.abandonar(userId, id);
        return ResponseEntity.ok().build();
    }
}
