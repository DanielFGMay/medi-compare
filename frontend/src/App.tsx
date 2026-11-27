import { useState, useEffect, useCallback } from 'react';
import { SearchBar } from './components/SearchBar';
import { ComparisonResults } from './components/ComparisonResults';
import { PriceComparison, SearchRequest } from './types/api';
import './styles/App.css';

/**
 * Componente principal de la aplicación Farmacompara
 * Comparador de precios de farmacias en Villahermosa/Cunduacán
 */
function App() {
  const [results, setResults] = useState<PriceComparison[] | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [userLocation, setUserLocation] = useState<{ latitude: number; longitude: number } | null>(null);
  const [lastSearch, setLastSearch] = useState<SearchRequest | null>(null);

  const API_BASE = import.meta.env.VITE_API_BASE || '/api';

  const handleSearch = useCallback(async (request: SearchRequest) => {
    setIsLoading(true);
    setError(null);
    setLastSearch(request);

    try {
      const params = new URLSearchParams();
      params.append('q', request.productName);
      if (request.latitude && request.longitude) {
        params.append('latitude', request.latitude.toString());
        params.append('longitude', request.longitude.toString());
      }
      if (request.radiusKm) {
        params.append('radiusKm', request.radiusKm.toString());
      }
      if (request.isRx) {
        params.append('isRx', 'true');
      }

      const response = await fetch(`${API_BASE}/compare?${params.toString()}`);

      if (!response.ok) {
        if (response.status === 204) {
          setResults([]);
          return;
        }
        throw new Error(`Error ${response.status}: ${response.statusText}`);
      }

      const data: PriceComparison[] = await response.json();
      setResults(data);
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Error desconocido al buscar';
      setError(message);
      setResults(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const handleLocationChange = useCallback((location: { latitude: number; longitude: number } | null) => {
    setUserLocation(location);
  }, []);

  const handleVisitStore = useCallback((url: string) => {
    window.open(url, '_blank', 'noopener,noreferrer');
  }, []);

  const handleRetry = useCallback(() => {
    if (lastSearch) {
      handleSearch(lastSearch);
    }
  }, [lastSearch, handleSearch]);

  // Cargar ubicación por defecto (Villahermosa) al iniciar
  useEffect(() => {
    setUserLocation({ latitude: 17.9869, longitude: -92.9303 });
  }, []);

  return (
    <div className="app">
      <header className="app-header">
        <div className="header-content">
          <h1>🩺 Farmacompara</h1>
          <p className="app-subtitle">Comparador de precios de farmacias en Villahermosa y Cunduacán</p>
        </div>
      </header>

      <main className="app-main">
        <div className="container">
          <SearchBar
            onSearch={handleSearch}
            isLoading={isLoading}
            userLocation={userLocation}
            onLocationChange={handleLocationChange}
          />

          {error && (
            <div className="error-banner" role="alert">
              <p>{error}</p>
              <button onClick={handleRetry} className="retry-button">Reintentar</button>
            </div>
          )}

          <ComparisonResults
            results={results}
            isLoading={isLoading}
            error={error}
            onVisitStore={handleVisitStore}
          />
        </div>
      </main>

      <footer className="app-footer">
        <div className="footer-content">
          <p>Datos de precios obtenidos mediante scraping público de sitios web de farmacias.</p>
          <p>Sucursales importadas desde OpenStreetMap (Overpass API).</p>
          <p className="disclaimer">
            ⚠️ Los precios son referenciales y pueden variar. Verifique en la tienda antes de comprar.
            Los medicamentos con receta requieren prescripción médica.
          </p>
        </div>
      </footer>
    </div>
  );
}

export default App;