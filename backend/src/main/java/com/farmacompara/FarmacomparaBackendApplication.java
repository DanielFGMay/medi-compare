package com.farmacompara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de arranque de la aplicación Spring Boot.
 * Punto de entrada que inicializa el contexto de Spring y escanea
 * todos los paquetes bajo com.farmacompara para detectar componentes
 * (controllers, services, repositories, etc.).
 */
@SpringBootApplication
public class FarmacomparaBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmacomparaBackendApplication.class, args);
    }
}