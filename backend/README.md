# Backend Farmacompara

Comparador de precios de farmacias - API REST con Spring Boot 3.x + PostgreSQL + PostGIS.

## Requisitos

- Java 17
- Maven 3.9+
- PostgreSQL con PostGIS (Neon o local)

## Configuración

### Variables de entorno

Copiar `.env.example` a `.env` y llenar los valores:

```bash
cp .env.example ../.env
```

### Variables necesarias

| Variable | Descripción |
|----------|-------------|
| `DB_URL` | JDBC URL de conexión PostgreSQL (ej: Neon) |
| `DB_USER` | Usuario de la base de datos |
| `DB_PASSWORD` | Contraseña de la base de datos |
| `JWT_SECRET` | Secret para firmar tokens JWT (128 chars mín) |

## Ejecutar localmente

```bash
# Desde el directorio backend/
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | /api/health | Verifica conectividad básica |
| GET | /api/health/detailed | Verifica conectividad con detalles |

## Estructura del proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/farmacompara/
│   │   │   ├── controller/      # Endpoints REST
│   │   │   ├── service/         # Lógica de negocio
│   │   │   ├── repository/      # Acceso a datos
│   │   │   ├── model/           # Entidades JPA
│   │   │   ├── dto/             # Objetos de transferencia
│   │   │   ├── scraper/         # Scrapers de precios
│   │   │   ├── config/          # Configuraciones
│   │   │   └── security/        # JWT, filtros
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/    # Flyway
│   └── test/                    # Tests JUnit 5
├── pom.xml
└── README.md
```

## Build

```bash
# Compilar
./mvnw clean package

# Ejecutar JAR
java -jar target/farmacompara-backend-0.0.1-SNAPSHOT.jar
```

## Despliegue en Render (plan gratuito)

1. Crear nuevo Web Service
2. Conectar repositorio
3. Build Command: `cd backend && ./mvnw -B package -DskipTests`
4. Start Command: `java -jar target/farmacompara-backend-0.0.1-SNAPSHOT.jar`
5. Add env vars desde archivo .env

## Próximos pasos

- [ ] Configuración CORS
- [ ] Spring Security + JWT
- [ ] Scrapers reales (Guadalajara, Del Ahorro, Similares)
- [ ] PostGIS para búsquedas geográficas