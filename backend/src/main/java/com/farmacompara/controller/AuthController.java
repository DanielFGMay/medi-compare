package com.farmacompara.controller;

import com.farmacompara.config.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador de autenticación para desarrollo
 * Genera tokens JWT de prueba para endpoints admin
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtService jwtService;

    /**
     * Genera un token JWT de prueba para desarrollo
     * Solo para uso en desarrollo - NO usar en producción
     */
    @PostMapping("/dev-token")
    public ResponseEntity<Map<String, String>> generateDevToken(@RequestParam(defaultValue = "admin") String username) {
        String token = jwtService.generateToken(username);
        return ResponseEntity.ok(Map.of("token", token, "type", "Bearer"));
    }
}