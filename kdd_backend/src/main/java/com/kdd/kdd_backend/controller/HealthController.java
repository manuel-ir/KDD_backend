package com.kdd.kdd_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador de comprobacion del estado del servidor.
 *
 * Expone un endpoint GET /api/health que devuelve un JSON simple
 * confirmando que el servidor esta en marcha. Es util para verificar
 * la conexion con Postman o herramientas de monitorizacion antes
 * de probar el resto de endpoints.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }
}
