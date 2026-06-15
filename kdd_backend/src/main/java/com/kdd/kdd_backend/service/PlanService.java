package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.CrearPlanDto;
import com.kdd.kdd_backend.model.PertenenciaPlanComunidad;
import com.kdd.kdd_backend.model.PertenenciaPlanComunidadId;
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
    private final PertenenciaPlanComunidadRepository pertenenciaPlanComunidadRepository;
    private final ComunidadRepository comunidadRepository;
    private final ValoracionRepository valoracionRepository;

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
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
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

        if (dto.getComunidadId() != null) {
            Comunidad comunidad = comunidadRepository.findById(dto.getComunidadId())
                    .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
            PertenenciaPlanComunidad ppc = new PertenenciaPlanComunidad();
            ppc.setId(new PertenenciaPlanComunidadId(plan.getId(), dto.getComunidadId()));
            ppc.setPlan(plan);
            ppc.setComunidad(comunidad);
            ppc.setEstado("confirmado");
            pertenenciaPlanComunidadRepository.save(ppc);
        }

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
        participacion.setEstado("pendiente");
        participacionRepository.save(participacion);
    }

    public List<ParticipanteDto> getParticipantes(Long planId) {
        return participacionRepository.findByIdPlanIdAndEstado(planId, "confirmado")
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

    public List<ParticipanteDto> getSolicitudes(Long planId, Long adminId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(adminId)) {
            throw new RuntimeException("Solo el anfitrión puede ver las solicitudes");
        }
        return participacionRepository.findByIdPlanIdAndEstado(planId, "pendiente")
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

    public void confirmarParticipante(Long adminId, Long planId, Long usuarioId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(adminId)) {
            throw new RuntimeException("Solo el anfitrión puede confirmar participantes");
        }
        Participacion p = participacionRepository
                .findByIdPlanIdAndEstado(planId, "pendiente")
                .stream()
                .filter(x -> x.getUsuario().getId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        p.setEstado("confirmado");
        participacionRepository.save(p);
    }

    public void rechazarParticipante(Long adminId, Long planId, Long usuarioId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(adminId)) {
            throw new RuntimeException("Solo el anfitrión puede rechazar participantes");
        }
        participacionRepository.deleteByIdUsuarioIdAndIdPlanId(usuarioId, planId);
    }

    public List<PlanDto> getPlanesComunidad(Long comunidadId, Long userId) {
        return pertenenciaPlanComunidadRepository.findByIdComunidadId(comunidadId)
                .stream()
                .map(ppc -> toDto(ppc.getPlan(), userId))
                .collect(Collectors.toList());
    }

    public List<PlanDto> misPlanes(Long userId) {
        return planRepository.findProximosByUsuario(LocalDate.now(), userId)
                .stream()
                .map(p -> toDto(p, userId))
                .collect(Collectors.toList());
    }

    public List<PlanDto> getHistorial(Long userId) {
        return planRepository.findHistorialByUsuario(LocalDate.now(), userId)
                .stream()
                .map(p -> toDto(p, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarPlan(Long planId, Long userId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(userId)) {
            throw new RuntimeException("Solo el creador puede borrar el plan");
        }
        valoracionRepository.deleteByIdPlan(planId);
        participacionRepository.deleteByIdPlanId(planId);
        pertenenciaPlanComunidadRepository.deleteByIdPlanId(planId);
        planRepository.delete(plan);
    }

    @Transactional
    public PlanDto editarPlan(Long planId, Long userId, CrearPlanDto dto) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(userId)) {
            throw new RuntimeException("Solo el creador puede editar el plan");
        }
        if (dto.getTitulo() != null) plan.setTitulo(dto.getTitulo());
        if (dto.getDescripcion() != null) plan.setDescripcion(dto.getDescripcion());
        if (dto.getFechaEvento() != null) plan.setFechaEvento(dto.getFechaEvento());
        if (dto.getHoraEvento() != null) plan.setHoraEvento(dto.getHoraEvento());
        if (dto.getUbicacionTexto() != null) plan.setUbicacionTexto(dto.getUbicacionTexto());
        if (dto.getEdadMin() != null) plan.setEdadMin(dto.getEdadMin());
        if (dto.getEdadMax() != null) plan.setEdadMax(dto.getEdadMax());
        if (dto.getNumMaxPersonas() != null) plan.setNumMaxPersonas(dto.getNumMaxPersonas());
        if (dto.getIdioma() != null) plan.setIdioma(dto.getIdioma());
        if (dto.getLatitud() != null) plan.setLatitud(dto.getLatitud());
        if (dto.getLongitud() != null) plan.setLongitud(dto.getLongitud());
        if (dto.getCategoria() != null) {
            Categoria categoria = categoriaRepository.findAll().stream()
                    .filter(c -> c.getTipo().equalsIgnoreCase(dto.getCategoria()))
                    .findFirst()
                    .orElseGet(() -> {
                        Categoria nueva = new Categoria();
                        nueva.setTipo(dto.getCategoria());
                        return categoriaRepository.save(nueva);
                    });
            plan.setCategoria(categoria);
        }
        planRepository.save(plan);
        return toDto(plan, userId);
    }

    @Transactional
    public void abandonar(Long userId, Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        if (!participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, planId)) {
            throw new RuntimeException("No estás apuntado a este plan");
        }

        if (plan.getCreador() != null && plan.getCreador().getId().equals(userId)) {
            plan.setCreador(null);
            planRepository.save(plan);
        }

        participacionRepository.deleteByIdUsuarioIdAndIdPlanId(userId, planId);
    }

    @Transactional
    public void actualizarFotoPlan(Long planId, Long userId, String imagenUrl) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(userId)) {
            throw new RuntimeException("Solo el creador puede actualizar la foto del plan");
        }
        plan.setImagenUrl(imagenUrl);
        planRepository.save(plan);
    }

    private PlanDto toDto(Plan p, Long userId) {
        boolean esMiembro = userId != null && participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, p.getId());
        boolean esCreador = userId != null && p.getCreador() != null && p.getCreador().getId().equals(userId);
        boolean esPendiente = userId != null && !esCreador
                && participacionRepository.existsByIdUsuarioIdAndIdPlanIdAndEstado(userId, p.getId(), "pendiente");

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
                .latitud(p.getLatitud())
                .longitud(p.getLongitud())
                .imagenUrl(p.getImagenUrl())
                .anfitrionNombre(p.getCreador() != null ? p.getCreador().getNombre() : null)
                .anfitrionId(p.getCreador() != null ? p.getCreador().getId() : null)
                .numParticipantes(participacionRepository.countByIdPlanIdAndEstado(p.getId(), "confirmado"))
                .numApuntados(participacionRepository.countByIdPlanId(p.getId()))
                .miembro(esMiembro)
                .creador(esCreador)
                .pendiente(esPendiente)
                .build();
    }
}