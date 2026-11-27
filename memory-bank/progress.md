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
- [x] API del comparador (ComparatorController con endpoints de búsqueda y comparación)
- [x] SiteRecognizer para análisis de sitios web
- [x] BaseScraper con arquitectura Strategy pattern
- [x] FarmaciaSimilaresScraper (implementación ejemplo)
- [x] ScrapingScheduler con programación por cron
- [x] AdminController con endpoints de scraping manual
- [ ] Importación de sucursales Overpass (Villahermosa/Cunduacán)
- [ ] Scraper de Guadalajara, Del Ahorro y Similares (adaptar a estructuras HTML reales)

## Fase 3 — Núcleo
- [ ] Búsqueda de cercanía con PostGIS
- [x] API del comparador y filtros por categoría
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
- **Fase 1 · Cimientos: Pasos 2, 3 y 4 completados al 100%.**
- **Fase 2 · Datos reales: 90% completado.**
  - **Backend**: Spring Boot 3.2.5 creado con `pom.xml` (Web, JPA, Security, Validation, Actuator, Flyway, Swagger, Quartz, Jsoup, DevTools).
    - Endpoint `/api/health` implementado y probado exitosamente en http://localhost:8091/api/health.
    - Conexión a Neon PostgreSQL verificada: Flyway validó 3 migraciones exitosamente (V1, V2, V3 con datos seed).
    - JPA/Hibernate conectado a Neon (PostgreSQL 18.7 con PostGIS).
    - Estructura en capas creada (controller, service, repository, model, dto, config, scraper).
    - **Modelos JPA**: Chain, Branch, Category, Product, Offer creados con relaciones.
    - **Repositorios JPA**: ChainRepository, BranchRepository, CategoryRepository, ProductRepository, OfferRepository.
    - **Servicios JPA**: ChainService, BranchService, CategoryService, ProductService, OfferService.
    - **Scraper Architecture**: SiteRecognizer, Scraper, BaseScraper, ScraperService, ScrapingScheduler.
    - **Controllers**: HealthController, ComparatorController, AdminController, ChainController, CategoryController.
    - Configuración CORS y Security básica implementadas.
    - Docker configurado para despliegue futuro.
    - Seed data cargado: 3 categorías, 3 cadenas, 4 sucursales, 5 productos, 7 ofertas.
    - **Problema identificado**: SQL syntax error en `OfferRepository.findLatestOfferPerChainForProduct` - DISTINCT ON no funciona en HQL.
    - **Solución aplicada**: Cambiado a native query para el método problemático.
    - **Problema pendiente**: Backend running con security config antiguo - necesita reinicio físico.
  - **Frontend**: Vite + React + TypeScript configurado y buildeado exitosamente.
    - Build de producción generado en `dist/`.
    - Proxy configurado para `/api` → `http://localhost:8091`.
    - Tipos API definidos en `src/types/api.ts`.
    - Componentes base creados (HealthCheck, App.tsx, main.tsx).
  - **CI/CD**: Workflow de GitHub Actions creado en `.github/workflows/ci.yml`.
