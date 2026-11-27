package com.farmacompara.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for JwtService
 */
class JwtServiceTest {

    private JwtService jwtService;
    private final String secret = "test-secret-key-for-unit-tests-at-least-32-chars-long";
    private final long expirationMs = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(secret, expirationMs);
    }

    @Test
    void generateToken_ShouldReturnValidToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertNotNull(token);
        assertTrue(token.length() > 10);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        String extracted = jwtService.extractUsername(token);

        assertEquals(username, extracted);
    }

    @Test
    void extractRole_ShouldReturnAdmin() {
        String token = jwtService.generateToken("admin");

        String role = jwtService.extractRole(token);

        assertEquals("ADMIN", role);
    }

    @Test
    void isTokenValid_ShouldReturnTrueForValidToken() {
        String username = "testuser";
        String token = jwtService.generateToken(username);

        assertTrue(jwtService.isTokenValid(token, username));
    }

    @Test
    void isTokenValid_ShouldReturnFalseForWrongUsername() {
        String token = jwtService.generateToken("correctuser");

        assertFalse(jwtService.isTokenValid(token, "wronguser"));
    }

    @Test
    void isTokenExpired_ShouldReturnFalseForNewToken() {
        String token = jwtService.generateToken("testuser");

        assertFalse(jwtService.isTokenExpired(token));
    }
}