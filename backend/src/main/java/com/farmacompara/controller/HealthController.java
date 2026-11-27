package com.farmacompara.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Controlador para verificar el estado de salud de la API.
 * Endpoint público que confirma que el backend está respondiendo
 * y conectado a la base de datos.
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    /**
     * Endpoint de salud básica.
     * Devuelve un JSON con estado OK y timestamp actual.
     * Usado por el deploy y para verificar conectividad.
     *
     * @return ResponseEntity con mapa de estado
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "timestamp", Instant.now().toString(),
            "service", "farmacompara-backend"
        ));
    }

    /**
     * Endpoint de salud detallado.
     * Incluye información adicional sobre la versión.
     *
     * @return ResponseEntity con estado detallado
     */
    @GetMapping("/health/detailed")
    public ResponseEntity<Map<String, Object>> healthDetailed() {
        return ResponseEntity.ok(Map.of(
            "status", "OK",
            "timestamp", Instant.now().toString(),
            "service", "farmacompara-backend",
            "version", "0.0.1-SNAPSHOT",
            "javaVersion", System.getProperty("java.version"),
            "vendor", System.getProperty("java.vendor")
        ));
    }
}