package com.farmacompara.controller;

import com.farmacompara.config.SseConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

/**
 * Controlador para SSE (Server-Sent Events)
 * Permite actualizaciones en tiempo real de precios
 */
@RestController
@RequestMapping("/api/sse")
public class SseController {

    @Autowired
    private SseConfig sseConfig;

    /**
     * Endpoint SSE para conectarse a actualizaciones en tiempo real
     * GET /api/sse/connect?userId=xxx
     */
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestParam String userId) {
        SseEmitter emitter = sseConfig.getOrCreateEmitter(userId);

        // Enviar evento de conexión inicial
        try {
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(System.currentTimeMillis()))
                    .name("connected")
                    .data(Map.of("status", "connected", "userId", userId)));
        } catch (Exception e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    /**
     * Endpoint para probar broadcast manual (solo admin/dev)
     */
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcast(
            @RequestParam String message,
            @RequestHeader(value = "Authorization", required = false) String auth) {

        // Verificar token admin (opcional en dev)
        if (auth != null && auth.startsWith("Bearer ")) {
            sseConfig.broadcast(Map.of("message", message, "timestamp", System.currentTimeMillis()));
            return ResponseEntity.ok(Map.of("sent", true, "clients", sseConfig.emitters.size()));
        }

        return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
    }

    /**
     * Obtener número de clientes conectados
     */
    @GetMapping("/clients")
    public ResponseEntity<Map<String, Object>> getConnectedClients() {
        return ResponseEntity.ok(Map.of("connectedClients", sseConfig.emitters.size()));
    }
}