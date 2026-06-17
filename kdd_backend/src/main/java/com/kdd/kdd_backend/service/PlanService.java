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
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio con toda la logica de negocio de los planes (actividades).
 *
 * Funcionalidades principales:
 * - Listar planes disponibles, filtrando los que ya han terminado.
 * - Crear, editar y eliminar planes.
 * - Gestionar la participacion: unirse y abandonar.
 * - Controlar el aforo: maximo de participantes confirmados.
 * - Validar la edad de los participantes segun el rango del plan.
 * - Marcar presencia: con logica estricta de tiempo de inicio.
 * - Obtener la lista de participantes y sus datos.
 */
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

    public List<PlanDto> listarPlanes(Long userId) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        return planRepository.findAll()
                .stream()
                .filter(p -> {
                    if (p.getFechaEvento() == null) return true;
                    if (p.getFechaEvento().isAfter(hoy)) return true;
                    if (p.getFechaEvento().isBefore(hoy)) return false;
                    if (p.getHoraHasta() != null) return !p.getHoraHasta().isBefore(ahora);
                    if (p.getHoraEvento() != null) return !p.getHoraEvento().isBefore(ahora);
                    return true;
                })
                .map(p -> toDto(p, userId))
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
                .fechaEvento(parseFecha(dto.getFechaEvento()))
                .horaEvento(parseHora(dto.getHoraEvento()))
                .horaHasta(parseHora(dto.getHoraHasta()))
                .ubicacionTexto(dto.getUbicacionTexto())
                .edadMin(dto.getEdadMin())
                .edadMax(dto.getEdadMax())
                .numMaxPersonas(dto.getNumMaxPersonas())
                .idioma(dto.getIdioma())
                .latitud(dto.getLatitud())
                .longitud(dto.getLongitud())
                .fotoPlanUrl(dto.getFotoPlanUrl())
                .creador(creador)
                .categoria(categoria)
                .build();

        plan = planRepository.save(plan);

        Participacion participacion = new Participacion();
        participacion.setId(new ParticipacionId(userId, plan.getId()));
        participacion.setUsuario(creador);
        participacion.setPlan(plan);
        participacion.setEstado("confirmado");
        participacion.setAcompanantes(dto.getAcompanantes() != null ? dto.getAcompanantes() : 1);
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

    public PlanDto editarPlan(Long userId, Long planId, CrearPlanDto dto) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(userId)) {
            throw new RuntimeException("Solo el creador puede editar el plan");
        }
        plan.setTitulo(dto.getTitulo());
        plan.setDescripcion(dto.getDescripcion());
        plan.setFechaEvento(parseFecha(dto.getFechaEvento()));
        plan.setHoraEvento(parseHora(dto.getHoraEvento()));
        plan.setHoraHasta(parseHora(dto.getHoraHasta()));
        plan.setUbicacionTexto(dto.getUbicacionTexto());
        plan.setEdadMin(dto.getEdadMin());
        plan.setEdadMax(dto.getEdadMax());
        plan.setNumMaxPersonas(dto.getNumMaxPersonas());
        plan.setIdioma(dto.getIdioma());
        plan.setLatitud(dto.getLatitud());
        plan.setLongitud(dto.getLongitud());
        if (dto.getFotoPlanUrl() != null) plan.setFotoPlanUrl(dto.getFotoPlanUrl());
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
        return toDto(planRepository.save(plan), userId);
    }

    @Transactional
    public void eliminarPlan(Long userId, Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(userId)) {
            throw new RuntimeException("Solo el creador puede eliminar el plan");
        }
        long otrosParticipantes = participacionRepository.countByIdPlanId(planId) - 1;
        if (otrosParticipantes > 0) {
            throw new RuntimeException("No puedes eliminar el plan mientras haya " + otrosParticipantes + " participante" + (otrosParticipantes == 1 ? "" : "s") + " apuntado" + (otrosParticipantes == 1 ? "" : "s"));
        }
        participacionRepository.deleteByIdPlanId(planId);
        planRepository.deleteById(planId);
    }

    public void unirse(Long userId, Long planId) {
        if (participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, planId)) {
            throw new RuntimeException("Ya estás apuntado a este plan");
        }

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        if (usuario.getFechaNacimiento() != null && (plan.getEdadMin() != null || plan.getEdadMax() != null)) {
            int edad = Period.between(usuario.getFechaNacimiento(), LocalDate.now()).getYears();
            if (plan.getEdadMin() != null && edad < plan.getEdadMin()) {
                throw new RuntimeException("No cumples la edad mínima de este plan (" + plan.getEdadMin() + " años)");
            }
            if (plan.getEdadMax() != null && edad > plan.getEdadMax()) {
                throw new RuntimeException("No cumples la edad máxima de este plan (" + plan.getEdadMax() + " años)");
            }
        }

        int participantesActuales = participacionRepository.countByIdPlanIdAndEstado(planId, "confirmado");
        if (plan.getNumMaxPersonas() != null && participantesActuales >= plan.getNumMaxPersonas()) {
            throw new RuntimeException("El plan está completo");
        }

        Participacion participacion = new Participacion();
        participacion.setId(new ParticipacionId(userId, planId));
        participacion.setUsuario(usuario);
        participacion.setPlan(plan);
        participacion.setEstado("confirmado");
        participacion.setAcompanantes(1);
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
                    Double media = valoracionRepository.findMediaPuntuacionByIdValorado(u.getId());
                    return ParticipanteDto.builder()
                            .id(u.getId())
                            .nombre(u.getNombre())
                            .nombreUsuario(u.getNombreUsuario())
                            .edad(edad)
                            .descripcion(u.getDescripcion())
                            .fotoPerfil(u.getFotoPerfil())
                            .presente(p.isPresente())
                            .puntuacion(media)
                            .acompanantes(p.getAcompanantes())
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
        return java.util.Collections.emptyList();
    }

    public void marcarPresente(Long adminId, Long planId, Long usuarioId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));

        boolean planHaComenzado;
        if (plan.getFechaEvento() == null) {
            planHaComenzado = true;
        } else {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaPlan = plan.getFechaEvento();
            if (fechaPlan.isBefore(hoy)) {
                planHaComenzado = true;
            } else if (fechaPlan.isAfter(hoy)) {
                planHaComenzado = false;
            } else {
                if (plan.getHoraEvento() != null) {
                    planHaComenzado = !LocalTime.now().isBefore(plan.getHoraEvento());
                } else {
                    planHaComenzado = true;
                }
            }
        }

        boolean esAutoConfirmacion = adminId.equals(usuarioId);
        boolean hayCreador = plan.getCreador() != null;
        boolean esAdmin = hayCreador && plan.getCreador().getId().equals(adminId);

        if (!esAdmin) {
            if (!planHaComenzado) {
                String cuandoEmpieza = plan.getHoraEvento() != null
                        ? "a las " + plan.getHoraEvento().toString().substring(0, 5)
                        : "en la fecha del plan";
                throw new RuntimeException("El plan aún no ha comenzado. Podrás confirmar tu asistencia " + cuandoEmpieza);
            }
            if (hayCreador) {
                if (!esAutoConfirmacion) {
                    throw new RuntimeException("Solo el anfitrión puede confirmar la presencia de otros");
                }
            }
            if (!participacionRepository.existsByIdUsuarioIdAndIdPlanIdAndEstado(adminId, planId, "confirmado")) {
                throw new RuntimeException("Debes estar apuntado al plan para confirmar presencia");
            }
        }
        Participacion p = participacionRepository
                .findByIdPlanIdAndEstado(planId, "confirmado")
                .stream()
                .filter(x -> x.getUsuario().getId().equals(usuarioId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Participante no encontrado"));
        p.setPresente(!p.isPresente());
        participacionRepository.save(p);
    }

    public void confirmarParticipante(Long adminId, Long planId, Long usuarioId) {
        marcarPresente(adminId, planId, usuarioId);
    }

    public void rechazarParticipante(Long adminId, Long planId, Long usuarioId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan no encontrado"));
        if (plan.getCreador() == null || !plan.getCreador().getId().equals(adminId)) {
            throw new RuntimeException("Solo el anfitrión puede eliminar participantes");
        }
        participacionRepository.deleteByIdUsuarioIdAndIdPlanId(usuarioId, planId);
    }

    public List<PlanDto> getPlanesComunidad(Long comunidadId, Long userId) {
        LocalDate hoy = LocalDate.now();
        LocalTime ahora = LocalTime.now();
        return pertenenciaPlanComunidadRepository.findByIdComunidadId(comunidadId)
                .stream()
                .filter(ppc -> {
                    Plan p = ppc.getPlan();
                    if (p.getFechaEvento() == null) return true;
                    if (p.getFechaEvento().isAfter(hoy)) return true;
                    if (p.getFechaEvento().isBefore(hoy)) return false;
                    if (p.getHoraHasta() != null) return !p.getHoraHasta().isBefore(ahora);
                    if (p.getHoraEvento() != null) return !p.getHoraEvento().isBefore(ahora);
                    return true;
                })
                .map(ppc -> toDto(ppc.getPlan(), userId))
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

        if (!participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, planId)) {
            throw new RuntimeException("No estás apuntado a este plan");
        }

        if (plan.getCreador() != null && plan.getCreador().getId().equals(userId)) {
            plan.setCreador(null);
            planRepository.save(plan);
        }

        participacionRepository.deleteByIdUsuarioIdAndIdPlanId(userId, planId);
    }

    private PlanDto toDto(Plan plan, Long userId) {
        int numParticipantes = participacionRepository.countByIdPlanIdAndEstado(plan.getId(), "confirmado");
        boolean esMiembro = participacionRepository.existsByIdUsuarioIdAndIdPlanId(userId, plan.getId());
        boolean esCreador = plan.getCreador() != null && plan.getCreador().getId().equals(userId);

        String nombreAnfitrion = null;
        if (plan.getCreador() != null) {
            nombreAnfitrion = plan.getCreador().getNombreUsuario() != null
                    ? plan.getCreador().getNombreUsuario()
                    : plan.getCreador().getNombre();
        }

        return PlanDto.builder()
                .id(plan.getId())
                .titulo(plan.getTitulo())
                .descripcion(plan.getDescripcion())
                .fechaEvento(plan.getFechaEvento() != null ? plan.getFechaEvento().toString() : null)
                .horaEvento(plan.getHoraEvento() != null ? plan.getHoraEvento().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : null)
                .horaHasta(plan.getHoraHasta() != null ? plan.getHoraHasta().format(DateTimeFormatter.ofPattern("HH:mm:ss")) : null)
                .ubicacionTexto(plan.getUbicacionTexto())
                .latitud(plan.getLatitud())
                .longitud(plan.getLongitud())
                .edadMin(plan.getEdadMin())
                .edadMax(plan.getEdadMax())
                .numMaxPersonas(plan.getNumMaxPersonas())
                .idioma(plan.getIdioma())
                .fotoPlanUrl(plan.getFotoPlanUrl())
                .categoria(plan.getCategoria() != null ? plan.getCategoria().getTipo() : null)
                .anfitrionId(plan.getCreador() != null ? plan.getCreador().getId() : null)
                .anfitrionNombre(nombreAnfitrion)
                .numParticipantes(numParticipantes)
                .numApuntados(numParticipantes)
                .miembro(esMiembro)
                .creador(esCreador)
                .build();
    }

    private LocalDate parseFecha(String s) {
        if (s == null || s.isBlank()) return null;
        try { return LocalDate.parse(s); } catch (Exception e) { return null; }
    }

    private LocalTime parseHora(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            String valor = s.length() > 5 ? s.substring(0, 5) : s;
            return LocalTime.parse(valor, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) { return null; }
    }
}