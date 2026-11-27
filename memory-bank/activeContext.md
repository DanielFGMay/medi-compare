# Active Context

## Fase actual
Fase 1 — Cimientos. Paso 2: Backend Spring Boot conectado a Neon.

## Reglas Permanentes del Mentor
Estas reglas aplican en TODOS los chats de este proyecto, sin que el usuario deba repetirlas.

1. **MODO AUTÓNOMO Y VISUAL**: Tienes permiso total para crear/editar archivos, ejecutar comandos en Git Bash y usar Playwright MCP para abrir el navegador real, inspeccionar tiendas (Farmacias Guadalajara, Del Ahorro, Similares) y probar visualmente el frontend en localhost. No pidas confirmaciones intermedias; actúa directo.
2. **AHORRO DE TOKENS**: Nunca leas node_modules, target, .git ni archivos masivos. Si no existe .claudeignore, créalo excluyendo esas carpetas y .env. Resume respuestas en terminal a máximo 5 líneas al terminar cada bloque.
3. **ESTRATEGIA LOCAL -> NUBE GRATUITA**: Primero construye TODO funcionando 100% local (Spring Boot + Neon + Scrapers + React en localhost). Solo luego prepara archivos de despliegue gratuito (Dockerfile + Render para backend, Cloudflare Pages para frontend).

## Qué se decidió
Ver techContext.md. Ciudad piloto: Villahermosa / Cunduacán, Tabasco. Paquete base: `com.farmacompara`.

## Siguiente paso
Fase 2 continuación: 
1. Reiniciar backend para aplicar cambios de SecurityConfig (permisos para /api/compare)
2. Probar endpoint /api/compare con datos seed
3. Implementar UI del comparador en frontend
4. Verificar funcionalidad completa

## Preguntas abiertas
- Hosting gratuito del backend (Render) y estrategia del cron.

## Notas
- Fase 1 Paso 1 completado: Neon + PostGIS configurado, ramas main/test listas.
## Cierre de Sesión
Se activa cuando el usuario escribe "Hasta aquí llegamos por hoy":
- Paso A: explicar a detalle y paso a paso todo lo construido en la sesión.
- Paso B: plantear un reto técnico estilo entrevista basado exactamente en lo hecho hoy (endpoint extra, bug intencional, validación) para que el usuario lo resuelva solo.
- Actualizar progress.md y activeContext.md.

## Objetivo profesional y alcance extra
- Meta: pasar pruebas técnicas de Jr. Full Stack. Proyecto 1.
- Demostrar: Java 17, Spring Boot, Spring Security + JWT, CORS, JPA/Hibernate, API REST documentada con Swagger (springdoc), pruebas JUnit 5 + Mockito, PostgreSQL + PostGIS, React + TS + Tailwind + Vite, Docker, flujo Git profesional.
- Todo gratuito o Free Tier.
