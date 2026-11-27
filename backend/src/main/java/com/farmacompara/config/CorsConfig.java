package com.farmacompara.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Configuración CORS para permitir peticiones desde el frontend.
 * En desarrollo se permite CORS global, en producción se restringe a dominios específicos.
 */
@Configuration
public class CorsConfig {

    /**
     * Configuración CORS básica que permite:
     * - Origen: cualquier origen (ajustar en producción)
     * - Métodos: GET, POST, PUT, DELETE, OPTIONS
     * - Headers: Authorization, Content-Type, etc.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}