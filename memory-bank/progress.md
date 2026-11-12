# Progress

## Fase 0 — Planificación
- [x] Evaluar fuentes de precios, geo y tiempo real
- [x] Ajustar decisiones: precios reales con scraper, infra gratuita en la nube, arquitectura en capas, flujo Git
- [x] Inicializar memory-bank

## Fase 1 — Cimientos
- [ ] Paso 1: Git, ramas main/test/feature, estructura base, Neon + PostGIS
- [ ] Paso 2: backend Spring Boot base conectado a Neon (rama feature/backend-base)
- [ ] Paso 3: frontend Vite + React + TS + Tailwind
- [ ] Paso 4: CI básico en GitHub Actions

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