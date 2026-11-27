# Progress

## Fase 0 — Planificación
- [x] Evaluar fuentes de precios, geo y tiempo real
- [x] Ajustar decisiones: precios reales con scraper, infra gratuita en la nube, arquitectura en capas, flujo Git
- [x] Inicializar memory-bank

## Fase 1 — Cimientos
- [x] Paso 1: Git, ramas main/test/feature, estructura base, Neon + PostGIS
- [x] Paso 2: backend Spring Boot base conectado a Neon (rama feature/backend-base)
- [x] Paso 3: frontend Vite + React + TS + Tailwind (completado - servido en http://localhost:3000)
- [x] Paso 4: CI básico en GitHub Actions

## Fase 2 — Datos reales
- [ ] Reconocimiento de sitios (robots.txt, términos, red, Jsoup vs Playwright)
- [ ] Migraciones Flyway (chain, branch, product, offer)
- [ ] Importación de sucursales Overpass (Villahermosa/Cunduacán)
- [ ] Scraper de Guadalajara, Del Ahorro y Similares
- [ ] Scheduler + endpoint admin para disparar scraping

## Fase 3 — Núcleo
- [ ] Búsqueda de cercanía con PostGIS
- [ ] API del comparador y filtros por categoría
- [ ] UI del comparador y botón de redirección

## Fase 4 — Tiempo real y seguridad
- [ ] Detección de cambios de precio y SSE
- [ ] Auth JWT con rol ADMIN
- [ ] Cliente EventSource en React

## Fase 5 — Cierre MVP
- [ ] Tests clave
- [ ] Despliegue gratuito (BD, backend, frontend) y link en CV
- [ ] README con capturas y arquitectura

## Historial
- Fase 0 cerrada con decisiones en techContext.md.
## Estado actual (27 Nov 2026)
- **Fase 1 · Cimientos: Paso 2, 3 y 4 completados al 100%.**
  - **Backend**: Spring Boot 3.2.5 creado con `pom.xml` (Web, JPA, Security, Validation, Actuator, Flyway, Swagger).
    - Endpoint `/api/health` implementado y probado exitosamente en http://localhost:8081/api/health.
    - Conexión a Neon PostgreSQL verificada: Flyway validó 1 migración exitosamente.
    - JPA/Hibernate conectado a Neon (PostgreSQL 18.7 con PostGIS).
    - Estructura en capas creada (controller, service, repository, model, dto, config, scraper).
    - Configuración CORS y Security básica implementadas.
    - Docker configurado para despliegue futuro.
  - **Frontend**: Vite + React + TypeScript configurado.
    - Servido en http://localhost:3000 (proxy API → backend:8081).
    - Componente HealthCheck verificando conectividad con backend.
    - Tipos API definidos en `src/types/api.ts` para futuras integraciones.
    - Proxy configurado para `/api` → `http://localhost:8081`.
    - Estructura de estilos base creada.
  - **CI/CD**: Workflow de GitHub Actions creado en `.github/workflows/ci.yml`.
