# Active Context

## Fase actual
Fase 1 — Cimientos. Paso 1: Git, ramas main/test/feature, estructura base y BD PostGIS en la nube.

## Reglas Permanentes del Mentor
Estas reglas aplican en TODOS los chats de este proyecto, sin que el usuario deba repetirlas.

1. Explicar TODO el código y los comandos. Cada vez que se entregue un archivo de código, configuración, SQL o un comando de terminal, explicar obligatoriamente:
   - Para qué sirve exactamente cada parte o bloque.
   - Qué cosas se pueden modificar o personalizar.
   - Qué pasa si se omite o borra esa parte (qué error daría o qué dejaría de funcionar).
2. Guiar paso a paso con el flujo de ramas de Git usando Git Bash: crear una rama feature/... desde test, trabajar y hacer commits limpios (Conventional Commits), fusionar en test (merge --no-ff), verificar, y solo entonces fusionar test en main. Explicar cómo moverse entre ramas y resolver dudas de Git en cada fase.
3. Todos los comandos de terminal son para Git Bash en Windows.
4. Ir fase por fase y paso por paso: Planificar -> MVP -> Escalar. No adelantar pasos sin confirmación.
5. Código backend en Java 17 + Spring Boot con arquitectura en capas (controller, service, repository, model/entity, dto, scraper, config); frontend en React + TypeScript.
6. Precios siempre reales (scraper), nunca simulados. Guiar el scraping de forma responsable (ver techContext.md).
7. Nivel: desarrollador Jr. que ya desplegó apps reales. Sin explicaciones triviales, pero con buenas prácticas y código limpio.
8. Al terminar cada paso, actualizar progress.md y activeContext.md.

## Qué se decidió
Ver projectbrief.md y techContext.md. Ciudad piloto: Villahermosa / Cunduacán, Tabasco.

## Siguiente paso
Paso 2 de la Fase 1: rama feature/backend-base, generar el backend con Spring Initializr dentro de backend/, conectar a Neon con variables de entorno y verificar con un endpoint de salud.

## Preguntas abiertas
- Nombre definitivo del proyecto y paquete base Java.
- Neon vs Supabase (se prueba primero Neon).
- Hosting gratuito del backend (Render u otro) y estrategia del cron.

## Notas
- Al terminar Paso 1 el repo tiene main y test sincronizadas y la BD Neon con PostGIS activo.