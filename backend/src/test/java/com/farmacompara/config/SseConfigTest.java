package com.farmacompara.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SseConfig
 */
class SseConfigTest {

    private SseConfig sseConfig;

    @BeforeEach
    void setUp() {
        sseConfig = new SseConfig();
    }

    @Test
    void getOrCreateEmitter_ShouldCreateNewEmitter() {
        SseEmitter emitter = sseConfig.getOrCreateEmitter("user1");

        assertNotNull(emitter);
    }

    @Test
    void getOrCreateEmitter_ShouldReturnSameEmitterForSameUserId() {
        SseEmitter emitter1 = sseConfig.getOrCreateEmitter("user1");
        SseEmitter emitter2 = sseConfig.getOrCreateEmitter("user1");

        assertSame(emitter1, emitter2);
    }

    @Test
    void getOrCreateEmitter_ShouldCreateDifferentEmittersForDifferentUsers() {
        SseEmitter emitter1 = sseConfig.getOrCreateEmitter("user1");
        SseEmitter emitter2 = sseConfig.getOrCreateEmitter("user2");

        assertNotSame(emitter1, emitter2);
    }

    @Test
    void broadcast_ShouldNotThrowForEmptyEmitters() {
        assertDoesNotThrow(() -> sseConfig.broadcast(new HashMap<>(Map.of("test", "data"))));
    }

    @Test
    void removeEmitter_ShouldRemoveEmitterFromMap() {
        sseConfig.getOrCreateEmitter("user1");
        assertEquals(1, sseConfig.connectedClients());

        sseConfig.removeEmitter("user1");

        assertEquals(0, sseConfig.connectedClients());
    }

    @Test
    void connectedClients_ShouldReturnCorrectCount() {
        sseConfig.getOrCreateEmitter("user1");
        sseConfig.getOrCreateEmitter("user2");
        sseConfig.getOrCreateEmitter("user3");

        assertEquals(3, sseConfig.connectedClients());
    }

    @Test
    void getOrCreateEmitter_ShouldCreateClientWithClosedFlag() {
        // First call to create the emitter
        sseConfig.getOrCreateEmitter("user1");

        var client = sseConfig.emitters.get("user1");

        assertNotNull(client);
        assertFalse(client.closed.get());
    }
}