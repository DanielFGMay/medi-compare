package com.farmacompara.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad básica.
 * Permite acceso público a endpoints de salud y API pública.
 * Endpoints /api/admin/** requieren autenticación JWT.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configuración de la cadena de filtros de seguridad.
     *
     * @param http el objeto HttpSecurity para configurar la seguridad
     * @return el SecurityFilterChain configurado
     * @throws Exception si hay problemas en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF para API REST
            .csrf(csrf -> csrf.disable())

            // Configuración de autorización
            .authorizeHttpRequests(auth -> auth
                // Permitir acceso público a endpoints de salud
                .requestMatchers("/api/health", "/api/health/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Swagger UI público
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Endpoints admin requieren autenticación
                .requestMatchers("/api/admin/**").authenticated()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Configuración de HTTP Basic (temporal para desarrollo)
            .httpBasic(Customizer.withDefaults())

            // Configurar sesiones como stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    org.springframework.security.config.http.SessionCreationPolicy.STATELESS
                )
            );

        return http.build();
    }
}