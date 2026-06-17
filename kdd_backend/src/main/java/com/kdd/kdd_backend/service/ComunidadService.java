package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.ComunidadDto;
import com.kdd.kdd_backend.dto.CrearComunidadDto;
import com.kdd.kdd_backend.dto.MiembroComunidadDto;
import com.kdd.kdd_backend.model.*;

import java.time.LocalDate;
import java.time.Period;
import com.kdd.kdd_backend.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio con la logica de negocio de las comunidades.
 *
 * Permite crear comunidades, unirse y abandonarlas, consultar
 * la lista de miembros y los planes asociados. Valida la edad
 * del usuario al intentar unirse si la comunidad tiene restriccion.
 */
@Service
@RequiredArgsConstructor
public class ComunidadService {

    private final ComunidadRepository comunidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final PertenenciaComunidadRepository pertenenciaRepository;
    private final PertenenciaPlanComunidadRepository pertenenciaPlanComunidadRepository;

    public List<ComunidadDto> listar() {
        return comunidadRepository.findAll()
                .stream()
                .map(c -> toDto(c, null))
                .collect(Collectors.toList());
    }

    public ComunidadDto getDetalle(Long comunidadId, Long userId) {
        Comunidad comunidad = comunidadRepository.findById(comunidadId)
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
        return toDto(comunidad, userId);
    }

    public ComunidadDto crear(Long userId, CrearComunidadDto dto) {
        Usuario admin = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comunidad comunidad = Comunidad.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .ubicacion(dto.getUbicacion())
                .edadMin(dto.getEdadMin())
                .edadMax(dto.getEdadMax())
                .fotoComunidadUrl(dto.getFotoComunidadUrl())
                .categoria(dto.getCategoria())
                .admin(admin)
                .build();

        comunidad = comunidadRepository.save(comunidad);

        PertenenciaComunidad pertenencia = new PertenenciaComunidad();
        pertenencia.setId(new PertenenciaComunidadId(userId, comunidad.getId()));
        pertenencia.setUsuario(admin);
        pertenencia.setComunidad(comunidad);
        pertenencia.setEstado("confirmado");
        pertenenciaRepository.save(pertenencia);

        return toDto(comunidad, userId);
    }

    public void unirse(Long userId, Long comunidadId) {
        if (pertenenciaRepository.existsByIdUsuarioIdAndIdComunidadId(userId, comunidadId)) {
            throw new RuntimeException("Ya perteneces a esta comunidad");
        }

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Comunidad comunidad = comunidadRepository.findById(comunidadId)
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));

        if (usuario.getFechaNacimiento() != null) {
            int edad = Period.between(usuario.getFechaNacimiento(), LocalDate.now()).getYears();
            if (comunidad.getEdadMin() != null && edad < comunidad.getEdadMin())
                throw new RuntimeException("No cumples la edad mínima de esta comunidad (" + comunidad.getEdadMin() + " años)");
            if (comunidad.getEdadMax() != null && edad > comunidad.getEdadMax())
                throw new RuntimeException("No cumples la edad máxima de esta comunidad (" + comunidad.getEdadMax() + " años)");
        }

        PertenenciaComunidad pertenencia = new PertenenciaComunidad();
        pertenencia.setId(new PertenenciaComunidadId(userId, comunidadId));
        pertenencia.setUsuario(usuario);
        pertenencia.setComunidad(comunidad);
        pertenencia.setEstado("confirmado");
        pertenenciaRepository.save(pertenencia);
    }

    @Transactional
    public void abandonar(Long userId, Long comunidadId) {
        Comunidad comunidad = comunidadRepository.findById(comunidadId)
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));

        if (comunidad.getAdmin() != null && comunidad.getAdmin().getId().equals(userId)) {
            throw new RuntimeException("El admin no puede abandonar su propia comunidad");
        }

        if (!pertenenciaRepository.existsByIdUsuarioIdAndIdComunidadId(userId, comunidadId)) {
            throw new RuntimeException("No perteneces a esta comunidad");
        }

        pertenenciaRepository.deleteByIdUsuarioIdAndIdComunidadId(userId, comunidadId);
    }

    public List<ComunidadDto> misComunidades(Long userId) {
        return pertenenciaRepository.findByIdUsuarioId(userId)
                .stream()
                .map(p -> toDto(p.getComunidad(), userId))
                .collect(Collectors.toList());
    }

    public List<MiembroComunidadDto> getMiembros(Long comunidadId) {
        return pertenenciaRepository.findByIdComunidadId(comunidadId)
                .stream()
                .map(p -> {
                    Usuario u = p.getUsuario();
                    Integer edad = null;
                    if (u.getFechaNacimiento() != null) {
                        edad = Period.between(u.getFechaNacimiento(), LocalDate.now()).getYears();
                    }
                    return MiembroComunidadDto.builder()
                            .id(u.getId())
                            .nombre(u.getNombre())
                            .fotoPerfil(u.getFotoPerfil())
                            .edad(edad)
                            .build();
                })
                .collect(Collectors.toList());
    }

    private ComunidadDto toDto(Comunidad c, Long userId) {
        boolean esMiembro = userId != null && pertenenciaRepository.existsByIdUsuarioIdAndIdComunidadId(userId, c.getId());
        boolean esAdmin = userId != null && c.getAdmin() != null && c.getAdmin().getId().equals(userId);

        return ComunidadDto.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .ubicacion(c.getUbicacion())
                .edadMin(c.getEdadMin())
                .edadMax(c.getEdadMax())
                .adminNombre(c.getAdmin() != null ? c.getAdmin().getNombre() : null)
                .adminId(c.getAdmin() != null ? c.getAdmin().getId() : null)
                .numMiembros(pertenenciaRepository.countByIdComunidadId(c.getId()))
                .miembro(esMiembro)
                .admin(esAdmin)
                .fotoComunidadUrl(c.getFotoComunidadUrl())
                .categoria(c.getCategoria())
                .build();
    }
}