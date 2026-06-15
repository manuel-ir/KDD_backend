package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.CrearPlanDto;
import com.kdd.kdd_backend.dto.ParticipanteDto;
import com.kdd.kdd_backend.dto.PlanDto;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/{id}/participantes")
    public ResponseEntity<List<ParticipanteDto>> participantes(@PathVariable Long id) {
        return ResponseEntity.ok(planService.getParticipantes(id));
    }

    @GetMapping("/{id}/solicitudes")
    public ResponseEntity<List<ParticipanteDto>> solicitudes(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.getSolicitudes(id, userId));
    }

    @PutMapping("/{id}/participantes/{usuarioId}/confirmar")
    public ResponseEntity<Void> confirmar(Authentication auth, @PathVariable Long id, @PathVariable Long usuarioId) {
        Long userId = (Long) auth.getPrincipal();
        planService.confirmarParticipante(userId, id, usuarioId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/participantes/{usuarioId}")
    public ResponseEntity<Void> rechazar(Authentication auth, @PathVariable Long id, @PathVariable Long usuarioId) {
        Long userId = (Long) auth.getPrincipal();
        planService.rechazarParticipante(userId, id, usuarioId);
        return ResponseEntity.ok().build();
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

    @PutMapping("/{id}/foto")
    public ResponseEntity<Void> actualizarFoto(Authentication auth, @PathVariable Long id,
                                               @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        planService.actualizarFotoPlan(id, userId, body.get("imagenUrl"));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/historial")
    public ResponseEntity<List<PlanDto>> historial(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.getHistorial(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        planService.eliminarPlan(id, userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDto> editar(Authentication auth, @PathVariable Long id,
                                          @RequestBody CrearPlanDto dto) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.editarPlan(id, userId, dto));
    }
}
