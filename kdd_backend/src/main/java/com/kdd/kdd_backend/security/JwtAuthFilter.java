package com.kdd.kdd_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de seguridad que intercepta todas las peticiones HTTP entrantes
 * y comprueba si llevan un token JWT valido en la cabecera Authorization.
 *
 * Si el token es valido, extrae el identificador del usuario y lo registra
 * en el contexto de seguridad de Spring, de forma que los controladores
 * puedan saber quien esta haciendo la peticion.
 *
 * Si no hay token o es invalido, la peticion pasa sin autenticar.
 * Spring Security se encargara de bloquearla si el endpoint lo requiere.
 *
 * Extiende OncePerRequestFilter para garantizar que el filtro se ejecuta
 * exactamente una vez por peticion, sin repeticiones.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // Servicio que se usa para validar y leer el contenido del token
    private final JwtService jwtService;

    /**
     * Logica principal del filtro. Se ejecuta con cada peticion HTTP.
     *
     * Proceso:
     * 1. Lee la cabecera Authorization de la peticion.
     * 2. Si no existe o no empieza por "Bearer ", deja pasar la peticion sin autenticar.
     * 3. Extrae el token (quita el prefijo "Bearer ").
     * 4. Si el token es valido, registra al usuario en el contexto de seguridad.
     * 5. En cualquier caso, pasa la peticion al siguiente filtro de la cadena.
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Leemos la cabecera de autorizacion
        final String authHeader = request.getHeader("Authorization");

        // Si no hay cabecera o no tiene el formato esperado, dejamos pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Quitamos el prefijo "Bearer " (7 caracteres) para quedarnos solo con el token
        final String jwt = authHeader.substring(7);

        // Validamos el token y, si es correcto, autenticamos al usuario
        if (jwtService.isTokenValid(jwt)) {
            Long userId = jwtService.extractUserId(jwt);

            // Creamos un objeto de autenticacion con el id del usuario como principal.
            // No necesitamos contrasena ni roles detallados para esta aplicacion.
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    java.util.Collections.emptyList()
            );

            // Anadimos los detalles de la peticion (IP, sesion, etc.) al objeto de autenticacion
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Registramos la autenticacion en el contexto de seguridad de Spring
            // A partir de aqui, cualquier controlador puede obtener el userId con getPrincipal()
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // Pasamos la peticion al siguiente elemento de la cadena de filtros
        filterChain.doFilter(request, response);
    }
}
