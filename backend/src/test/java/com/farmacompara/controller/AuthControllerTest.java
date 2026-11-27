package com.farmacompara.controller;

import com.farmacompara.config.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AuthController
 */
class AuthControllerTest {

    private JwtService jwtService;
    private AuthController authController;
    private final String secret = "test-secret-key-for-unit-tests-at-least-32-chars-long";
    private final long expirationMs = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expirationMs);
        authController = new AuthController();
        ReflectionTestUtils.setField(authController, "jwtService", jwtService);
    }

    @Test
    void generateDevToken_ShouldReturnToken() {
        String username = "admin";

        ResponseEntity<Map<String, String>> response = authController.generateDevToken(username);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().get("token"));
        assertEquals("Bearer", response.getBody().get("type"));

        // Verify the token is valid
        String token = response.getBody().get("token");
        assertTrue(jwtService.isTokenValid(token, username));
    }

    @Test
    void generateDevToken_ShouldUseDefaultUsernameWhenNotProvided() {
        // Simulate query param with default value by calling with explicit "admin"
        ResponseEntity<Map<String, String>> response = authController.generateDevToken("admin");

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody().get("token"));
        assertEquals("Bearer", response.getBody().get("type"));

        // Verify the token is valid
        String token = response.getBody().get("token");
        assertTrue(jwtService.isTokenValid(token, "admin"));
    }
}