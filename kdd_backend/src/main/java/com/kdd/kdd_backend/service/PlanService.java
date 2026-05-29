package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.CrearPlanDto;
import com.kdd.kdd_backend.dto.PlanDto;
import com.kdd.kdd_backend.model.*;
import com.kdd.kdd_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ParticipacionRepository participacionRepository;

    public List<PlanDto> listarPlanes() {
        return planRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PlanDto getDetalle(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        return toDto(plan);
    }

    public PlanDto crearPlan(Long userId, CrearPlanDto dto) {
        Usuario creador = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Categoria categoria = categoriaRepository.findAll()
                .stream()
                .filter(c -> c.getTipo().equalsIgnoreCase(dto.getCategoria()))
                .findFirst()
                .orElseGet(() -> {
                    Categoria nueva = new Categoria();
                    nueva.setTipo(dto.getCategoria());
                    return categoriaRepository.save(nueva);
                });

        Plan plan = Plan.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .fechaEvento(dto.getFechaEvento())
                .horaEvento(dto.getHoraEvento())
                .ubicacionTexto(dto.getUbicacionTexto())
                .edadMin(dto.getEdadMin())
                .edadMax(dto.getEdadMax())
                .numMaxPersonas(dto.getNumMaxPersonas())
                .idioma(dto.getIdioma())
                .creador(creador)
                .categoria(categoria)
                .build();

        plan = planRepository.save(plan);

        Participacion participacion = new Participacion();
        participacion.setId(new ParticipacionId(userId, plan.getId()));
        participacion.setUsuario(creador);
        participacion.setPlan(plan);
        participacion.setEstado("confirmado");
        participacionRepository.save(participacion);

        return toDto(plan);
    }

    public void unirse(Long userId, Long planId) {
        if (participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, planId)) {
            throw new RuntimeException("Ya estás apuntado a este plan");
        }

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        int actuales = participacionRepository.countByIdPlanId(planId);
        if (plan.getNumMaxPersonas() != null && actuales >= plan.getNumMaxPersonas()) {
            throw new RuntimeException("El plan está completo");
        }

        Participacion participacion = new Participacion();
        participacion.setId(new ParticipacionId(userId, planId));
        participacion.setUsuario(usuario);
        participacion.setPlan(plan);
        participacion.setEstado("pendiente");
        participacionRepository.save(participacion);
    }

    public List<PlanDto> misPlanes(Long userId) {
        return participacionRepository.findByIdUsuarioId(userId)
                .stream()
                .map(p -> toDto(p.getPlan()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void abandonar(Long userId, Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        if (plan.getCreador() != null && plan.getCreador().getId().equals(userId)) {
            throw new RuntimeException("El creador no puede abandonar su propio plan");
        }

        if (!participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, planId)) {
            throw new RuntimeException("No estás apuntado a este plan");
        }

        participacionRepository.deleteByIdUsuarioIdAndIdPlanId(userId, planId);
    }

    private PlanDto toDto(Plan p) {
        return PlanDto.builder()
                .id(p.getId())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                .categoria(p.getCategoria() != null ? p.getCategoria().getTipo() : null)
                .fechaEvento(p.getFechaEvento())
                .horaEvento(p.getHoraEvento())
                .ubicacionTexto(p.getUbicacionTexto())
                .edadMin(p.getEdadMin())
                .edadMax(p.getEdadMax())
                .numMaxPersonas(p.getNumMaxPersonas())
                .idioma(p.getIdioma())
                .anfitrionNombre(p.getCreador() != null ? p.getCreador().getNombre() : null)
                .anfitrionId(p.getCreador() != null ? p.getCreador().getId() : null)
                .numParticipantes(participacionRepository.countByIdPlanId(p.getId()))
                .build();
    }
}
