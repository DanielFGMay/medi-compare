# Backend Farmacompara - Dockerfile para producción
# Usa JDK 17 (Spring Boot 3.x requiere al menos Java 17)
FROM eclipse-temurin:17-jre-alpine AS runtime

# Directorio de trabajo
WORKDIR /app

# Copiar JAR de builds (se construye en stage de compilación)
# Para desarrollo, usamos maven para compilar en tiempo de build

# Variables de entorno
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080

# Exponer puerto
EXPOSE 8080

# Crear usuario no root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup
USER appuser

# Copiar el JAR (en producción real, se usa multi-stage build)
# ESTE CONTENEDOR ES PARA DESARROLLO
COPY --chown=appuser:appgroup mvnw ./
COPY --chown=appuser:appgroup .mvn ./
COPY --chown=appuser:appgroup pom.xml ./
COPY --chown=appuser:appgroup target/*.jar app.jar

# Comando de ejecución
ENTRYPOINT ["java", "-jar", "/app/app.jar"]