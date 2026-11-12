# Tech Context

## Stack
- Backend: Java 17, Spring Boot (Web, Security, Data JPA, Validation, Scheduling), JWT, Flyway, Jsoup (Playwright solo si una tienda lo exige).
- Frontend: React + TypeScript, Vite, Tailwind CSS, Axios, EventSource (SSE).
- BD: PostgreSQL + PostGIS en la nube (Neon o Supabase, plan gratuito).
- DevOps: Docker, GitHub Actions (CI y cron que dispara el scraper), variables de entorno.
- Entorno: Windows, VS Code, terminal Git Bash.

## Infraestructura gratuita (por confirmar planes vigentes)
- BD: Neon (PostGIS con CREATE EXTENSION). Alternativa: Supabase.
- Backend: candidato Render (plan gratuito con Docker). Se duerme tras inactividad, así que @Scheduled no es confiable: el scraping se dispara también con POST /api/admin/scrape/run (JWT ADMIN) llamado por un cron externo (GitHub Actions schedule). Alternativa: Oracle Cloud Always Free.
- Frontend: Cloudflare Pages.

## Arquitectura en capas (MVC / Spring Boot)
Paquete base provisional: com.farmacompara
- controller/  endpoints REST y SSE (solo reciben/devuelven DTOs, sin lógica de negocio)
- service/  lógica de negocio (comparador, detección de cambios de precio, geo)
- repository/  acceso a datos (Spring Data JPA, consultas nativas PostGIS)
- model/entity/  entidades JPA y enums
- dto/  objetos de petición/respuesta
- scraper/  Scraper de Precios Real: interfaz PriceScraper + GuadalajaraScraper, DelAhorroScraper, SimilaresScraper + ScraperScheduler (@Scheduled)
- client/  clientes de APIs externas: OverpassClient, NominatimClient
- config/  SecurityConfig, CorsConfig, SchedulingConfig, propiedades
- security/  JwtService, JwtAuthFilter
- exception/  GlobalExceptionHandler
Regla: controller -> service -> repository. El controller nunca llama al repository directamente; las entidades no se exponen en la API.

## Decisiones técnicas
1. Precios: Scraper de Precios Real en Java. Sucursales: API Overpass. Sin datos simulados.
2. Geo: PostGIS, columna geography(Point,4326) con índice GiST, consultas nativas ST_DWithin/ST_Distance. Lat/lon como double en la entidad (sin Hibernate Spatial). Plan B: Haversine + bounding box.
3. Tiempo real: SSE con SseEmitter, endpoint público, heartbeat cada 15-30 s, evento price-updated. Flujo: scraper -> servicio compara con el último precio -> publica evento Spring (ApplicationEventPublisher) -> SseService lo emite.
4. Seguridad: JWT solo para endpoints admin en V1; lectura pública.
5. Modelo: precio por cadena (no por sucursal); la UI muestra la sucursal más cercana de esa cadena.
6. Migraciones con Flyway. Sin H2: se desarrolla contra la BD en la nube (Neon) o una BD Neon de pruebas.

## Reglas del scraper
- Antes de escribir cada scraper: revisar robots.txt y términos de uso, e inspeccionar la red del navegador por si hay endpoint JSON; decidir Jsoup vs Playwright.
- User-Agent identificable, pausa de 2-5 s entre peticiones, lista acotada de productos (watchlist), caché, backoff ante 403/429.
- Si una tienda bloquea: no evadir; marcar la oferta como desactualizada y mostrar la fecha de última consulta.
- Detección: baja de precio, nueva oferta, y posible error de precio (cambio brusco mayor a un umbral configurable).

## Modelo de datos (borrador)
chain, branch (con ubicación), product (categoría OTC/RX/GENERAL), offer (producto + cadena + precio + precio_promo + texto_promo + url + scraped_at), price_history (V2).

## Git
Ramas: main (estable), test (integración/verificación), feature/* (trabajo). Flujo: feature -> test -> main con merge --no-ff. Commits con Conventional Commits (feat, fix, chore, docs, refactor, test).

## Variables de entorno
DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET. Plantilla en .env.example; .env nunca se sube a Git.

## Límites externos
- Overpass: consultas por ciudad/bbox en jobs programados, nunca por petición de usuario.
- Nominatim: máx. 1 req/s, User-Agent propio, caché.

## Pendientes por verificar
- Planes gratuitos vigentes (Neon, Render, Cloudflare Pages).
- robots.txt y términos de uso de cada tienda; si exponen JSON interno.
- Plantilla de URL de búsqueda/producto por cadena.