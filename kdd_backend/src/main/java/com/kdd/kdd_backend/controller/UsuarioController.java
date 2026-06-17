package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.EditarUsuarioDto;
import com.kdd.kdd_backend.dto.UsuarioDto;
import com.kdd.kdd_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de operaciones sobre el perfil del usuario.
 *
 * Gestiona la consulta y edicion del perfil del usuario autenticado,
 * asi como la consulta del perfil de otros usuarios.
 *
 * Todos los endpoints requieren JWT valido en la cabecera Authorization.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> getMiPerfil(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(usuarioService.getPerfil(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UsuarioDto> editarMiPerfil(Authentication auth,
                                                      @RequestBody EditarUsuarioDto dto) {
        Long userId = (Long) auth.getPrincipal();
        return ResponseEntity.ok(usuarioService.editarPerfil(userId, dto));
    }
}
