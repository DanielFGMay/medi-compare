package com.farmacompara;

import com.farmacompara.config.JwtService;
import com.farmacompara.config.SseConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Simple test runner to verify JWT and SSE work
 */
@SpringBootApplication
public class SimpleTestRunner {

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext ctx = SpringApplication.run(SimpleTestRunner.class, args);

        System.out.println("\n=== Running Simple Tests ===\n");

        // Test 1: JwtService
        testJwtService(ctx);

        // Test 2: SseConfig
        testSseConfig(ctx);

        System.out.println("\n=== All Tests Passed ===\n");
        ctx.close();
    }

    private static void testJwtService(ConfigurableApplicationContext ctx) {
        JwtService jwtService = ctx.getBean(JwtService.class);

        // Test generateToken
        String token = jwtService.generateToken("admin");
        assert token != null && token.length() > 10 : "Token should be generated";

        // Test extractUsername
        String username = jwtService.extractUsername(token);
        assert "admin".equals(username) : "Username should be 'admin'";

        // Test isTokenValid
        assert jwtService.isTokenValid(token, "admin") : "Token should be valid";

        System.out.println("✅ JwtService tests passed");
    }

    private static void testSseConfig(ConfigurableApplicationContext ctx) {
        SseConfig sseConfig = ctx.getBean(SseConfig.class);

        // Test getOrCreateEmitter
        Object emitter = sseConfig.getOrCreateEmitter("test-user");
        assert emitter != null : "Should create emitter";

        // Test same emitter for same user
        Object emitter2 = sseConfig.getOrCreateEmitter("test-user");
        assert emitter == emitter2 : "Should return same emitter";

        // Test connectedClients count
        int count = sseConfig.connectedClients();
        assert count >= 1 : "Should have at least 1 connected client";

        System.out.println("✅ SseConfig tests passed");
    }
}