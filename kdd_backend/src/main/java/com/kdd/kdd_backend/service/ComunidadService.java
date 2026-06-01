package com.kdd.kdd_backend.service;

import com.kdd.kdd_backend.dto.ComunidadDto;
import com.kdd.kdd_backend.dto.CrearComunidadDto;
import com.kdd.kdd_backend.model.*;
import com.kdd.kdd_backend.repository.*;
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

    public List<ComunidadDto> listar() {
        return comunidadRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ComunidadDto getDetalle(Long comunidadId) {
        Comunidad comunidad = comunidadRepository.findById(comunidadId)
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));
        return toDto(comunidad);
    }

    public ComunidadDto crear(Long userId, CrearComunidadDto dto) {
        Usuario admin = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comunidad comunidad = Comunidad.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
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

        return toDto(comunidad);
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
        pertenencia.setEstado("pendiente");
        pertenenciaRepository.save(pertenencia);
    }

    private ComunidadDto toDto(Comunidad c) {
        return ComunidadDto.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .descripcion(c.getDescripcion())
                .edadMin(c.getEdadMin())
                .edadMax(c.getEdadMax())
                .adminNombre(c.getAdmin() != null ? c.getAdmin().getNombre() : null)
                .adminId(c.getAdmin() != null ? c.getAdmin().getId() : null)
                .numMiembros(pertenenciaRepository.countByIdComunidadId(c.getId()))
                .build();
    }
}
