package com.kdd.kdd_backend.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.kdd.kdd_backend.dto.AuthResponse;
import com.kdd.kdd_backend.dto.GoogleAuthRequest;
import com.kdd.kdd_backend.dto.LoginEmailDto;
import com.kdd.kdd_backend.dto.RegistroEmailDto;
import com.kdd.kdd_backend.model.Usuario;
import com.kdd.kdd_backend.repository.UsuarioRepository;
import com.kdd.kdd_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticacion con Google a traves de Firebase.
 *
 * Proceso de inicio de sesion:
 * 1. La app Android realiza Google Sign-In y obtiene un idToken de Google.
 * 2. Envia ese idToken al backend en POST /api/auth/google.
 * 3. Este servicio verifica el token con Firebase Admin SDK.
 * 4. Si es valido, busca al usuario en la base de datos por su googleId.
 * 5. Si no existe, lo crea con los datos del perfil de Google.
 * 6. Genera un JWT propio de la aplicacion y lo devuelve al cliente.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        FirebaseToken firebaseToken = verifyFirebaseToken(request.getIdToken());

        if (firebaseToken == null) {
            throw new RuntimeException("Token de Firebase invalido");
        }

        String uid        = firebaseToken.getUid();
        String email      = firebaseToken.getEmail();
        String nombre     = firebaseToken.getName();
        String fotoPerfil = firebaseToken.getPicture();

        // Detectar proveedor ("google" o "email")
        String proveedor = "email";
        try {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> firebaseClaims =
                    (java.util.Map<String, Object>) firebaseToken.getClaims().get("firebase");
            if (firebaseClaims != null) {
                String signInProvider = (String) firebaseClaims.get("sign_in_provider");
                if ("google.com".equals(signInProvider)) proveedor = "google";
            }
        } catch (Exception ignored) {}

        if (nombre == null || nombre.isBlank()) {
            nombre = email.split("@")[0];
        }

        Usuario usuario = findOrCreateUser(uid, email, nombre, fotoPerfil, proveedor);
        String jwt = jwtService.generateToken(usuario.getId(), usuario.getEmail());

        return AuthResponse.builder()
                .token(jwt)
                .userId(usuario.getId())
                .displayName(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }

    // Registro con email y contrasena (sin Firebase)
    public AuthResponse registrarConEmail(RegistroEmailDto dto) {
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new RuntimeException("El nombre es obligatorio");
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            throw new RuntimeException("El email no es valido");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 9) {
            throw new RuntimeException("La contrasena debe tener al menos 9 caracteres");
        }
        if (!dto.getPassword().matches(".*[A-Z].*")) {
            throw new RuntimeException("La contrasena debe tener al menos una letra mayuscula");
        }
        if (!dto.getPassword().matches(".*[a-z].*")) {
            throw new RuntimeException("La contrasena debe tener al menos una letra minuscula");
        }
        if (!dto.getPassword().matches(".*[0-9].*")) {
            throw new RuntimeException("La contrasena debe tener al menos un numero");
        }
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Ya existe una cuenta con ese email");
        }

        Usuario usuario = Usuario.builder()
                .nombre(dto.getNombre().trim())
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .proveedor("email")
                .build();
        usuario = usuarioRepository.save(usuario);

        String jwt = jwtService.generateToken(usuario.getId(), usuario.getEmail());
        return AuthResponse.builder()
                .token(jwt)
                .userId(usuario.getId())
                .displayName(usuario.getNombre())
                .email(usuario.getEmail())
                .build();
    }

    // Login con email y contrasena (sin Firebase)
    public AuthResponse loginConEmail(LoginEmailDto dto) {
        Usuario usuario = usuarioRepository.findByEmail(
                dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : ""
        ).orElseThrow(() -> new RuntimeException("No existe una cuenta con ese email"));

        if ("google".equals(usuario.getProveedor())) {
            throw new RuntimeException("Esta cuenta usa Google Sign-In. Inicia sesion con Google.");
        }
        if (usuario.getPassword() == null) {
            throw new RuntimeException("Esta cuenta no tiene contrasena. Registrate de nuevo.");
        }
        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contrasena incorrecta");
        }

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

    private Usuario findOrCreateUser(String firebaseUid, String email, String nombre, String fotoPerfil, String proveedor) {
        return usuarioRepository.findByGoogleId(firebaseUid)
                .orElseGet(() -> createUser(firebaseUid, email, nombre, fotoPerfil, proveedor));
    }

    private Usuario createUser(String firebaseUid, String email, String nombre, String fotoPerfil, String proveedor) {
        Usuario newUser = Usuario.builder()
                .googleId(firebaseUid)
                .email(email)
                .nombre(nombre)
                .fotoPerfil(fotoPerfil)
                .proveedor(proveedor)
                .build();
        return usuarioRepository.save(newUser);
    }
}
