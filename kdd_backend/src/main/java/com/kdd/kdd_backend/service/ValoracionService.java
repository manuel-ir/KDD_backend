package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.ValoracionDto;
import com.kdd.kdd_backend.model.*;
import com.kdd.kdd_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de valoraciones entre participantes de un plan.
 *
 * Un usuario solo puede valorar a otro si ambos tienen presente=true
 * en ese plan (modo estricto). Solo se permite una valoracion por par
 * de usuarios en un mismo plan. La puntuacion debe estar entre 1 y 5.
 */
@Service
@RequiredArgsConstructor
public class ValoracionService {

    private final ValoracionRepository valoracionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final ParticipacionRepository participacionRepository;

    public void valorar(Long valoradorId, ValoracionDto dto) {
        if (valoradorId.equals(dto.getIdValorado())) {
            throw new RuntimeException("No puedes valorarte a ti mismo");
        }

        Plan plan = planRepository.findById(dto.getIdPlan())
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        if (!participacionRepository.existsByIdUsuarioIdAndIdPlanIdAndPresenteTrue(valoradorId, dto.getIdPlan())) {
            throw new RuntimeException("Debes confirmar tu asistencia para poder valorar");
        }
        if (!participacionRepository.existsByIdUsuarioIdAndIdPlanIdAndPresenteTrue(dto.getIdValorado(), dto.getIdPlan())) {
            throw new RuntimeException("Solo puedes valorar a usuarios que hayan confirmado su asistencia");
        }

        ValoracionId id = new ValoracionId(valoradorId, dto.getIdValorado(), dto.getIdPlan());
        if (valoracionRepository.existsById(id)) {
            throw new RuntimeException("Ya has valorado a este usuario en este plan. No puedes valorarle dos veces");
        }

        if (dto.getPuntuacion() < 1 || dto.getPuntuacion() > 5) {
            throw new RuntimeException("La puntuación debe estar entre 1 y 5");
        }

        Usuario valorador = usuarioRepository.findById(valoradorId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Usuario valorado = usuarioRepository.findById(dto.getIdValorado())
                .orElseThrow(() -> new RuntimeException("Usuario valorado no encontrado"));

        Valoracion valoracion = new Valoracion();
        valoracion.setIdValorador(valoradorId);
        valoracion.setIdValorado(dto.getIdValorado());
        valoracion.setIdPlan(dto.getIdPlan());
        valoracion.setValorador(valorador);
        valoracion.setValorado(valorado);
        valoracion.setPlan(plan);
        valoracion.setPuntuacion(dto.getPuntuacion());
        valoracion.setComentario(dto.getComentario());

        valoracionRepository.save(valoracion);
    }
}