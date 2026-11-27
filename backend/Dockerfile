# Backend Farmacompara - Multi-stage Dockerfile para producción
# Usa JDK 17 (Spring Boot 3.x requiere al menos Java 17)

# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN ./mvnw package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:17-jre-alpine AS runtime
WORKDIR /app

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Exponer puerto
EXPOSE 8080

# Crear usuario no root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup
USER appuser

# Copiar el JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "/app/app.jar"]