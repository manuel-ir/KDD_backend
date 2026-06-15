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

        if (usuario.getEdad() != null) {
            if (comunidad.getEdadMin() != null && usuario.getEdad() < comunidad.getEdadMin()) {
                throw new RuntimeException("No cumples la edad mínima para esta comunidad");
            }
            if (comunidad.getEdadMax() != null && usuario.getEdad() > comunidad.getEdadMax()) {
                throw new RuntimeException("Superas la edad máxima para esta comunidad");
            }
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

        if (!pertenenciaRepository.existsByIdUsuarioIdAndIdComunidadId(userId, comunidadId)) {
            throw new RuntimeException("No perteneces a esta comunidad");
        }

        if (comunidad.getAdmin() != null && comunidad.getAdmin().getId().equals(userId)) {
            comunidad.setAdmin(null);
            comunidadRepository.save(comunidad);
        }

        pertenenciaRepository.deleteByIdUsuarioIdAndIdComunidadId(userId, comunidadId);
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
                .build();
    }
}
