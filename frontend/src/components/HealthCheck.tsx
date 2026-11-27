import React, { useState, useEffect } from 'react';

interface HealthResponse {
  status: string;
  timestamp: string;
  service: string;
}

/**
 * Componente que muestra el estado de conexión con el backend
 * Usa el endpoint /api/health para verificar conectividad
 */
export const HealthCheck: React.FC = () => {
  const [health, setHealth] = useState<HealthResponse | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const checkHealth = async () => {
      try {
        const response = await fetch('/api/health');
        if (!response.ok) {
          throw new Error('Error en la respuesta del servidor');
        }
        const data: HealthResponse = await response.json();
        setHealth(data);
        setError(null);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Error desconocido');
      } finally {
        setLoading(false);
      }
    };

    checkHealth();
  }, []);

  if (loading) {
    return (
      <div className="health-card">
        <h3>Estado del backend</h3>
        <p className="status-checking">Verificando conexión...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="health-card health-error">
        <h3>Estado del backend</h3>
        <p className="status-error">❌ Error: {error}</p>
      </div>
    );
  }

  return (
    <div className="health-card health-ok">
      <h3>Estado del backend</h3>
      <p className="status-ok">✅ Conectado</p>
      <p className="health-details">
        <strong>Servicio:</strong> {health?.service}
      </p>
      <p className="health-details">
        <strong>Última actualización:</strong> {new Date(health?.timestamp || '').toLocaleString()}
      </p>
    </div>
  );
};