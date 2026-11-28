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
- [x] Reconocimiento de sitios (robots.txt, términos, red, Jsoup vs Playwright)
- [x] Migraciones Flyway (chain, branch, product, offer)
- [x] Modelos JPA y servicios para dominio
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
- [x] Despliegue gratuito (BD, backend, frontend) - CONFIGURACIÓN COMPLETADA
  - [x] Backend Dockerfile corregido para Render (rutas COPY desde raíz, PORT env var, chmod +x mvnw)
  - [x] Frontend configurado para Cloudflare Pages (wrangler.toml como Pages estático, VITE_API_URL)
  - [x] render.yaml actualizado (solo backend, frontend va a Cloudflare Pages)
  - [x] Variables de entorno preparadas (JWT_SECRET, DATABASE_URL, VITE_API_URL)
  - [x] Sin secretos expuestos en repositorio (.env no está versionado)
  - [x] **Corrección definitiva Render**: application-prod.yml lee ${SPRING_DATASOURCE_URL} con fallback a ${DB_URL} para Neon PostgreSQL
  - [x] **Corrección definitiva Cloudflare Pages**: wrangler.toml build command usa `npm install --prefix frontend && npm run build --prefix frontend`
- [ ] README con capturas y arquitectura

## Historial
- Fase 0 cerrada con decisiones en techContext.md.
- **27 Nov 2026: Correcciones de despliegue completadas al 100%**
  - **Error 1 CORREGIDO**: Dockerfile backend añadido `RUN chmod +x mvnw` para solucionar error 126 (permission denied) en Linux/Render.
  - **Error 2 CORREGIDO**: wrangler.toml reconfigurado como Cloudflare Pages estático (no Worker) con sección [pages] y build_output_dir = "frontend/dist".
  - Se configuró Cloudflare Pages para frontend como SPA estático (wrangler.toml).
  - Se actualizó render.yaml para solo el backend (frontend se desplegará por separado en Cloudflare Pages).
  - Se añadió cliente API con soporte para VITE_API_URL en producción.
- ## Estado actual (27 Nov 2026)
- **Fase 1 · Cimientos: Pasos 2, 3 y 4 completados al 100%.**
- **Fase 2 · Datos reales: Trabajo iniciado, migraciones y modelos base completados.**
  - **Backend**: Spring Boot 3.2.5 creado con `pom.xml` (Web, JPA, Security, Validation, Actuator, Flyway, Swagger).
    - Endpoint `/api/health` implementado y probado exitosamente en http://localhost:8081/api/health.
    - Conexión a Neon PostgreSQL verificada: Flyway validó 2 migraciones exitosamente (V1 y V2).
    - JPA/Hibernate conectado a Neon (PostgreSQL 18.7 con PostGIS).
    - Estructura en capas creada (controller, service, repository, model, dto, config, scraper).
    - **Modelos JPA**: Chain, Branch, Category, Product, Offer creados con relaciones.
    - **Repositorios JPA**: ChainRepository, BranchRepository, CategoryRepository, ProductRepository, OfferRepository.
    - **Servicios JPA**: ChainService, BranchService, CategoryService, ProductService, OfferService.
    - **SiteRecognizer**: Clase base para detección de sitios web y análisis de robots.txt.
    - Configuración CORS y Security básica implementadas.
    - Docker configurado para despliegue futuro.
  - **Frontend**: Vite + React + TypeScript configurado.
    - Servido en http://localhost:3000 (proxy API → backend:8081).
    - Componente HealthCheck verificando conectividad con backend.
    - Tipos API definidos en `src/types/api.ts` para futuras integraciones.
    - Proxy configurado para `/api` → `http://localhost:8081`.
    - Estructura de estilos base creada.
