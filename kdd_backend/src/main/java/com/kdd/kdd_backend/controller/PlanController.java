package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.CrearPlanDto;
import com.kdd.kdd_backend.dto.ParticipanteDto;
import com.kdd.kdd_backend.dto.PlanDto;
import com.kdd.kdd_backend.model.Categoria;
import com.kdd.kdd_backend.repository.CategoriaRepository;
import com.kdd.kdd_backend.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador de planes (actividades).
 *
 * Expone los endpoints REST para crear, listar, editar, eliminar y
 * gestionar la participacion en planes. Tambien incluye los endpoints
 * para marcar presencia y consultar participantes.
 *
 * Todos los endpoints requieren JWT valido.
 */
@RestController
@RequestMapping("/api/planes")
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;
    private final CategoriaRepository categoriaRepository;

    @GetMapping("/categorias")
    public ResponseEntity<List<String>> getCategorias() {
        List<String> tipos = categoriaRepository.findAll()
                .stream()
                .map(Categoria::getTipo)
                .sorted()
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(tipos);
    }

    @GetMapping
    public ResponseEntity<List<PlanDto>> listar(Authentication auth) {
        Long userId = auth != null ? (Long) auth.getPrincipal() : null;
        return ResponseEntity.ok(planService.listarPlanes(userId));
    }

    @GetMapping("/mis-planes")
    public ResponseEntity<List<PlanDto>> misPlanes(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.misPlanes(userId));
    }

    @GetMapping("/historial")
    public ResponseEntity<List<PlanDto>> historial(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.historialPlanes(userId));
    }

    @GetMapping("/mis-planes-creados")
    public ResponseEntity<List<PlanDto>> misPlanesCreados(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.planesCreados(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanDto> detalle(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.getDetalle(id, userId));
    }

    @PostMapping
    public ResponseEntity<?> crear(Authentication auth,
                                   @RequestBody CrearPlanDto dto) {
        Long userId = (Long) auth.getPrincipal();
        try {
            return ResponseEntity.ok(planService.crearPlan(userId, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanDto> editar(Authentication auth, @PathVariable Long id, @RequestBody CrearPlanDto dto) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(planService.editarPlan(userId, id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(Authentication auth, @PathVariable Long id) {
        Long userId = (Long) auth.getPrincipal();
        planService.eliminarPlan(userId, id);
        return ResponseEntity.noContent().build();
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

    @PatchMapping("/{id}/participantes/{usuarioId}/presente")
    public ResponseEntity<Void> marcarPresente(Authentication auth, @PathVariable Long id, @PathVariable Long usuarioId) {
        Long userId = (Long) auth.getPrincipal();
        planService.marcarPresente(userId, id, usuarioId);
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
}