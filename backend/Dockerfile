# Backend Farmacompara - Multi-stage Dockerfile para producción
# Usa JDK 17 (Spring Boot 3.x requiere al menos Java 17)
# IMPORTANTE: Este Dockerfile está en backend/ y Render construye desde la raíz del repo

# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy maven wrapper and pom from backend/
COPY backend/mvnw .
COPY backend/.mvn .mvn
COPY backend/pom.xml .

# Fix permissions for mvnw (Error 126: permission denied in Linux)
RUN chmod +x mvnw

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY backend/src ./src
RUN ./mvnw package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Variables de entorno - Puerto configurable para Render (usa PORT env var)
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=${PORT:-8080}

# Exponer puerto
EXPOSE 8080

# Crear usuario no root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup
USER appuser

# Copiar el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Comando de ejecución - usa PORT de Render
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]