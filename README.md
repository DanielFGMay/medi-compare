# FarmaCompara

Comparador de precios reales de farmacias por geolocalización.

## Features

- **Backend**: Spring Boot 3.2.5, Java 17, PostgreSQL + PostGIS
- **Frontend**: React 18.3.1, TypeScript 5.5, Vite 5.4, Tailwind CSS
- **Security**: JWT Bearer token authentication
- **Real-time**: Server-Sent Events (SSE) for price updates
- **Deployment**: Docker multi-stage builds, Render, Cloudflare Pages

## Architecture

```
frontend/          # React SPA (Tailwind CSS)
  ├── src/
  ├── Dockerfile
  └── nginx.conf      # Production nginx config

backend/           # Spring Boot API
  ├── src/main/java/com/farmacompara/
  │   ├── config/       # JWT, Security, SSE config
  │   ├── controller/   # REST controllers
  │   ├── service/      # Business logic
  │   └── scraper/      # Price scraping
  ├── src/test/java/   # JUnit 5 + Mockito tests
  └── Dockerfile       # Multi-stage build
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /api/auth/dev-token | Generate dev JWT token |
| GET | /api/branches | Get branches (with optional distance filter) |
| GET | /api/offers | Get offers (with filters) |
| GET | /api/categories | Get all categories |
| GET | /api/chains | Get pharmacy chains |
| GET | /sse/updates | Server-Sent Events for real-time updates |
| POST | /api/admin/scrape | Trigger price scraping (admin only) |

## Development

### Prerequisites

- Java 17+ (JDK)
- Node.js 20+
- PostgreSQL 15+ with PostGIS extension
- Docker (for production builds)

### Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Run Tests

```bash
cd backend
./mvnw test
```

## Deployment

### Backend (Render)

```bash
# Render.yaml configures automatic deployment
# Set DATABASE_URL via Render Secrets Dashboard
```

### Frontend (Cloudflare Pages)

Build the frontend and deploy `dist/` folder to Cloudflare Pages, or use the Dockerfile with `render.yaml`.

## Environment Variables

| Variable | Description |
|----------|-------------|
| JWT_SECRET | Secret key for JWT token signing |
| JWT_EXPIRATION | Token expiration in milliseconds |
| DATABASE_URL | PostgreSQL connection URL (Render will set this) |
| SPRING_PROFILES_ACTIVE | Spring profile (dev/prod) |

## License

MIT
