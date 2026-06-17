package com.kdd.kdd_backend.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones para la API REST.
 *
 * Cuando cualquier servicio lanza una RuntimeException, este manejador
 * la intercepta y devuelve una respuesta HTTP con el codigo de estado
 * apropiado y un mensaje de error en formato JSON.
 *
 * De esta forma no hace falta tratar las excepciones en cada controlador
 * por separado: se gestionan todas en un solo lugar.
 *
 * Ejemplos de respuesta:
 *   - "no encontrado"  -> 404 Not Found
 *   - "Solo el..."     -> 403 Forbidden
 *   - "completo"       -> 409 Conflict
 *   - Otros            -> 400 Bad Request
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String mensaje = ex.getMessage() != null ? ex.getMessage() : "Error interno";

        HttpStatus status;
        if (mensaje.contains("no encontrado") || mensaje.contains("no encontrada")) {
            status = HttpStatus.NOT_FOUND;
        } else if (mensaje.contains("Solo el") || mensaje.contains("no puedes") || mensaje.contains("No puedes")) {
            status = HttpStatus.FORBIDDEN;
        } else if (mensaje.contains("Ya estás") || mensaje.contains("completo")) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.BAD_REQUEST;
        }

        return ResponseEntity.status(status).body(Map.of("error", mensaje));
    }
}
