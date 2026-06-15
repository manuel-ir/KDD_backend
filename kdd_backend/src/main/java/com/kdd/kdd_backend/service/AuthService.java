package com.kdd.kdd_backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.kdd.kdd_backend.dto.AuthResponse;
import com.kdd.kdd_backend.dto.GoogleAuthRequest;
import com.kdd.kdd_backend.model.Usuario;
import com.kdd.kdd_backend.repository.UsuarioRepository;
import com.kdd.kdd_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        FirebaseToken firebaseToken = verifyFirebaseToken(request.getIdToken());

        if (firebaseToken == null) {
            throw new RuntimeException("Token de Firebase invalido");
        }

        String uid      = firebaseToken.getUid();
        String email    = firebaseToken.getEmail();
        String nombre   = firebaseToken.getName();
        String fotoPerfil = firebaseToken.getPicture();

        if (nombre == null || nombre.isBlank()) {
            nombre = email.split("@")[0];
        }

        Usuario usuario = findOrCreateUser(uid, email, nombre, fotoPerfil);
        String jwt = jwtService.generateToken(usuario.getId(), usuario.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .userId(usuario.getId())
                .displayName(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }

    private FirebaseToken verifyFirebaseToken(String idToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            System.err.println("Error verificando token de Firebase: " + e.getMessage());
            return null;
        }
    }

    private Usuario findOrCreateUser(String googleId, String email, String nombre, String fotoPerfil) {
        return usuarioRepository.findByGoogleId(googleId)
                .orElseGet(() -> createUser(googleId, email, nombre, fotoPerfil));
    }

    private Usuario createUser(String googleId, String email, String nombre, String fotoPerfil) {
        Usuario newUser = Usuario.builder()
                .googleId(googleId)
                .email(email)
                .nombre(nombre)
                .fotoPerfil(fotoPerfil)
                .build();
        return usuarioRepository.save(newUser);
    }
}
