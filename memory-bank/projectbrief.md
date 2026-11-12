# Project Brief

## Nombre (provisional)
FarmaCompara — comparador de precios y promociones de farmacias por geolocalización.

## Objetivo
Proyecto estrella de portafolio para el CV: app web desplegada 100% en la nube (costo $0) que compara precios REALES de un mismo producto entre cadenas de farmacias (Farmacias Guadalajara, Del Ahorro, Similares), muestra la sucursal cercana y redirige a la compra oficial. Debe terminarse al 100%, con link vivo y README sólido.

## Principio de datos
- Cero precios simulados o inventados. Todo precio viene de las tiendas en línea oficiales mediante un Scraper de Precios Real.
- Cada precio guarda fecha/hora de consulta (scraped_at) y URL de origen; la UI muestra "precio consultado el ...".
- Las sucursales vienen de la API Overpass (OpenStreetMap).

## Requisitos funcionales
1. Geolocalización del navegador o búsqueda por código postal/ciudad.
2. Comparador: mismo producto en distintas cadenas, resalta el más barato, botón al enlace directo del producto en la tienda oficial y sucursal cercana.
3. Tiempo real: al detectar baja de precio, oferta o error de precio, el backend emite un evento SSE y el frontend se actualiza sin recargar.
4. Filtros: Sin receta, Con receta (solo informativo), General/cuidado personal.
5. Ingesta automatizada en Java: scraper programado (@Scheduled) para precios y job de importación Overpass para sucursales.

## Alcance
- MVP (V1): scraper real de las 3 cadenas para una lista inicial de productos (~50-100), sucursales reales de Villahermosa/Cunduacán (Tabasco), comparador, filtros, SSE, endpoint admin con JWT, despliegue gratuito en la nube, CI con GitHub Actions, README.
- V2: historial de precios y gráfica, detección de errores de precio con alertas, cuentas de usuario, favoritos y alertas por producto, expansión a todo México (código postal / geolocalización), tests de integración con Testcontainers.
- V3: extraer el scraper como microservicio, mensajería (Kafka/SQS), Redis para SSE multi-instancia y caché, AWS/Kubernetes, agente de IA (equivalentes genéricos).

## Fuera de alcance por ahora
Compra dentro de la app, pagos, receta digital, consejo médico.

## Restricciones
- Costo de infraestructura: $0 MXN (backend, frontend y BD en planes gratuitos).
- Medicamentos con receta: solo informativos, con aviso legal visible.
- Scraping responsable: respetar robots.txt y términos de uso, limitar la frecuencia, no evadir protecciones anti-bot.
- Respetar límites de Overpass y Nominatim.

## Ciudad piloto
Villahermosa / Cunduacán, Tabasco, con opción de expandir a todo México.