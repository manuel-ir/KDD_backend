package com.kdd.kdd_backend.controller;

import com.kdd.kdd_backend.dto.AuthResponse;
import com.kdd.kdd_backend.dto.GoogleAuthRequest;
import com.kdd.kdd_backend.dto.LoginEmailDto;
import com.kdd.kdd_backend.dto.RegistroEmailDto;
import com.kdd.kdd_backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticacion con Google.
 *
 * Recibe el token de Google Sign-In enviado por la app Android,
 * lo verifica con Firebase y devuelve un token JWT propio de la aplicacion.
 * A partir de ese momento el cliente incluye el JWT en todas las peticiones.
 *
 * Endpoint publico (no requiere autenticacion previa):
 *   POST /api/auth/google  ->  { token, userId, displayName, email }
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleAuth(@RequestBody GoogleAuthRequest request) {
        AuthResponse response = authService.authenticateWithGoogle(request);
        return ResponseEntity.ok(response);
    }

    // Registro con email y contrasena — sin necesidad de cuenta de Google
    @PostMapping("/registro")
    public ResponseEntity<?> registro(@RequestBody RegistroEmailDto dto) {
        try {
            AuthResponse response = authService.registrarConEmail(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    // Login con email y contrasena
    @PostMapping("/login-email")
    public ResponseEntity<?> loginEmail(@RequestBody LoginEmailDto dto) {
        try {
            AuthResponse response = authService.loginConEmail(dto);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }
}
