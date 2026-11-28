/**
 * Cliente API para comunicación con el backend
 * Usa VITE_API_URL en producción, proxy de Vite en desarrollo
 */

// En desarrollo, Vite proxy maneja /api -> localhost:8081
// En producción, usamos la variable de entorno VITE_API_URL
const getApiBase = (): string => {
  // En build de producción, VITE_API_URL se inyecta en tiempo de build
  if (import.meta.env.PROD && import.meta.env.VITE_API_URL) {
    return import.meta.env.VITE_API_URL;
  }
  // En desarrollo, usamos el proxy de Vite (/api)
  return '';
};

export const api = {
  async healthCheck(): Promise<HealthResponse> {
    const base = getApiBase();
    const response = await fetch(`${base}/api/health`);
    if (!response.ok) {
      throw new Error(`Health check failed: ${response.status}`);
    }
    return response.json();
  },
};

export interface HealthResponse {
  status: string;
  timestamp: string;
  service: string;
}