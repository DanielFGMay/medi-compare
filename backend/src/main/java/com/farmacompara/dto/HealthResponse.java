package com.farmacompara.dto;

import java.time.Instant;

/**
 * DTO para respuesta de salud del backend
 */
public class HealthResponse {
    private String status;
    private Instant timestamp;
    private String service;

    public HealthResponse() {
    }

    public HealthResponse(String status, Instant timestamp, String service) {
        this.status = status;
        this.timestamp = timestamp;
        this.service = service;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }
}