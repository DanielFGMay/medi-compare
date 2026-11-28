package com.farmacompara.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Configuración SSE para actualizaciones en tiempo real
 * Mantiene el registro de clientes conectados por userId.
 * SseEmitter (Spring 6.1) no expone su estado, así que lo rastreamos con AtomicBoolean.
 */
@Configuration
public class SseConfig {

    /** Emisor registrado junto con su flag de cierre */
    public static final class Client {
        public final SseEmitter emitter;
        public final AtomicBoolean closed = new AtomicBoolean(false);
        Client(SseEmitter emitter) { this.emitter = emitter; }
    }

    // Mapa para mantener conectados los clientes SSE por userId
    public final Map<String, Client> emitters = new ConcurrentHashMap<>();

    /**
     * Obtener o crear un SSE Emitter para el cliente actual
     */
    public SseEmitter getOrCreateEmitter(String userId) {
        Client existing = emitters.get(userId);
        if (existing != null && !existing.closed.get()) {
            return existing.emitter;
        }

        SseEmitter newEmitter = new SseEmitter(300_000L); // 5 minutos timeout
        Client client = new Client(newEmitter);
        emitters.put(userId, client);

        Runnable markClosed = () -> {
            client.closed.set(true);
            emitters.remove(userId, client);
        };
        newEmitter.onCompletion(markClosed);
        newEmitter.onTimeout(() -> {
            markClosed.run();
            try { newEmitter.complete(); } catch (Exception ignored) {}
        });
        newEmitter.onError((ex) -> markClosed.run());

        return newEmitter;
    }

    /**
     * Broadcast evento SSE a todos los clientes conectados activos
     */
    public void broadcast(Object event) {
        emitters.forEach((userId, client) -> {
            if (client.closed.get()) {
                emitters.remove(userId, client);
                return;
            }
            try {
                client.emitter.send(SseEmitter.event().data(event));
            } catch (Exception e) {
                client.closed.set(true);
                emitters.remove(userId, client);
                try { client.emitter.complete(); } catch (Exception ignored) {}
            }
        });
    }

    /**
     * Remover cliente del mapa
     */
    public void removeEmitter(String userId) {
        Client client = emitters.remove(userId);
        if (client != null) {
            client.closed.set(true);
            try { client.emitter.complete(); } catch (Exception ignored) {}
        }
    }

    /**
     * Número de clientes conectados
     */
    public int connectedClients() {
        return (int) emitters.values().stream().filter(c -> !c.closed.get()).count();
    }
}
