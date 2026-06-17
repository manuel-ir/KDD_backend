package com.kdd.kdd_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio encargado de la generacion y validacion de tokens JWT.
 *
 * Un token JWT (JSON Web Token) es una cadena de texto firmada que permite
 * al servidor identificar al usuario sin necesidad de consultar la base de datos
 * en cada peticion. Se compone de tres partes: cabecera, datos (claims) y firma.
 *
 * La clave secreta y el tiempo de expiracion se leen desde application.yaml.
 */
@Service
public class JwtService {

    // Clave secreta usada para firmar y verificar los tokens (definida en application.yaml)
    @Value("${app.jwt.secret}")
    private String secret;

    // Tiempo de vida del token en milisegundos (por defecto, 24 horas)
    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Convierte la clave secreta (String) en un objeto criptografico HMAC-SHA
     * que la libreria jjwt puede usar para firmar y verificar tokens.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Genera un token JWT con el identificador del usuario y su correo electronico.
     * El token incluye la fecha de emision y la fecha de expiracion.
     *
     * @param userId identificador unico del usuario en la base de datos
     * @param email  correo electronico del usuario
     * @return cadena de texto con el token JWT firmado
     */
    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .subject(String.valueOf(userId))    // el "sujeto" del token es el id del usuario
                .claim("email", email)              // dato extra que viaja dentro del token
                .issuedAt(new Date())               // fecha de creacion
                .expiration(new Date(System.currentTimeMillis() + expirationMs)) // fecha de expiracion
                .signWith(getSigningKey())           // firma con HMAC-SHA
                .compact();                         // genera la cadena final
    }

    /**
     * Extrae el identificador de usuario del token.
     * Se guarda en el campo "subject" del token.
     *
     * @param token token JWT enviado por el cliente
     * @return identificador numerico del usuario
     */
    public Long extractUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    /**
     * Extrae el correo electronico almacenado dentro del token.
     *
     * @param token token JWT enviado por el cliente
     * @return correo electronico del usuario
     */
    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    /**
     * Comprueba si un token es valido: tiene la firma correcta y no ha expirado.
     *
     * @param token token JWT a validar
     * @return true si el token es valido, false si ha expirado o esta malformado
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // Token malformado, firma incorrecta o expirado
            return false;
        }
    }

    /**
     * Parsea el token JWT y devuelve el conjunto de datos (claims) que contiene.
     * Lanza excepcion si la firma no coincide o el token esta corrompido.
     *
     * @param token token JWT a parsear
     * @return objeto Claims con todos los datos del token
     */
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
