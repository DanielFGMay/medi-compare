-- Migración inicial para la tabla de verificación
-- Se eliminará y se reemplazará con el esquema completo en migraciones futuras

CREATE TABLE health_check (
    id SERIAL PRIMARY KEY,
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar un registro de prueba
INSERT INTO health_check (checked_at) VALUES (CURRENT_TIMESTAMP);

-- Verificar que PostGIS esté disponible (no se crea extensión aquí, se asume que ya está activa en Neon)