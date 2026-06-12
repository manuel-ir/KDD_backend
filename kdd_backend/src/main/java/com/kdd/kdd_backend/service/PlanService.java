package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.CrearPlanDto;
import com.kdd.kdd_backend.dto.ParticipanteDto;
import com.kdd.kdd_backend.dto.PlanDto;
import com.kdd.kdd_backend.model.*;
import com.kdd.kdd_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
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
                .map(p -> toDto(p, null))
                .collect(Collectors.toList());
    }

    public PlanDto getDetalle(Long planId, Long userId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        return toDto(plan, userId);
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

        return toDto(plan, userId);
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
        participacion.setEstado("confirmado");
        participacionRepository.save(participacion);
    }

    public List<ParticipanteDto> getParticipantes(Long planId) {
        return participacionRepository.findByIdPlanId(planId)
                .stream()
                .map(p -> {
                    Usuario u = p.getUsuario();
                    Integer edad = u.getFechaNacimiento() != null
                            ? Period.between(u.getFechaNacimiento(), LocalDate.now()).getYears()
                            : null;
                    return ParticipanteDto.builder()
                            .id(u.getId())
                            .nombre(u.getNombre())
                            .edad(edad)
                            .descripcion(u.getDescripcion())
                            .fotoPerfil(u.getFotoPerfil())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<PlanDto> misPlanes(Long userId) {
        return participacionRepository.findByIdUsuarioId(userId)
                .stream()
                .map(p -> toDto(p.getPlan(), userId))
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

    private PlanDto toDto(Plan p, Long userId) {
        boolean esMiembro = userId != null && participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, p.getId());
        boolean esCreador = userId != null && p.getCreador() != null && p.getCreador().getId().equals(u