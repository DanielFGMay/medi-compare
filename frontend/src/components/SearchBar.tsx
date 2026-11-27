import { useState, KeyboardEvent, ChangeEvent } from 'react';
import { SearchRequest, CategoryFilter } from '../types/api';

interface SearchBarProps {
  onSearch: (request: SearchRequest) => void;
  isLoading: boolean;
  userLocation?: { latitude: number; longitude: number } | null;
  onLocationChange?: (location: { latitude: number; longitude: number } | null) => void;
}

const CATEGORIES: CategoryFilter[] = [
  { id: 'all', label: 'Todos', value: 'all' },
  { id: 'general', label: 'General', value: 'general' },
  { id: 'otc', label: 'Sin receta (OTC)', value: 'otc' },
  { id: 'rx', label: 'Con receta', value: 'rx' },
];

export function SearchBar({ onSearch, isLoading, userLocation, onLocationChange }: SearchBarProps) {
  const [query, setQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<'all' | 'otc' | 'rx' | 'general'>('all');
  const [radius, setRadius] = useState(10);
  const [useGeolocation, setUseGeolocation] = useState(false);
  const [locationError, setLocationError] = useState<string | null>(null);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!query.trim()) return;

    const request: SearchRequest = {
      productName: query.trim(),
      radiusKm: radius,
      isRx: selectedCategory === 'rx',
    };

    if (selectedCategory === 'otc') {
      request.categoryId = 'otc';
    }

    // Usar geolocalización si está habilitada
    if (useGeolocation && userLocation) {
      request.latitude = userLocation.latitude;
      request.longitude = userLocation.longitude;
    }

    onSearch(request);
  };

  const handleGeolocation = () => {
    if (!navigator.geolocation) {
      setLocationError('Geolocalización no soportada en este navegador');
      return;
    }

    setLocationError(null);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const location = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        };
        onLocationChange?.(location);
        setUseGeolocation(true);
      },
      (error) => {
        let msg = 'Error al obtener ubicación';
        switch (error.code) {
          case error.PERMISSION_DENIED:
            msg = 'Permiso de ubicación denegado';
            break;
          case error.POSITION_UNAVAILABLE:
            msg = 'Ubicación no disponible';
            break;
          case error.TIMEOUT:
            msg = 'Tiempo de espera agotado';
            break;
        }
        setLocationError(msg);
        setUseGeolocation(false);
      },
      { enableHighAccuracy: true, timeout: 10000, maximumAge: 0 }
    );
  };

  return (
    <form onSubmit={handleSubmit} className="search-bar">
      <div className="search-input-group">
        <label htmlFor="search-query" className="sr-only">Buscar medicamento o producto</label>
        <input
          id="search-query"
          type="text"
          value={query}
          onChange={(e: ChangeEvent<HTMLInputElement>) => setQuery(e.target.value)}
          onKeyDown={(e: KeyboardEvent<HTMLInputElement>) => {
            if (e.key === 'Enter') handleSubmit(e);
          }}
          placeholder="Buscar medicamento (ej: paracetamol, ibuprofeno...)"
          disabled={isLoading}
          className="search-input"
          autoFocus
        />
        <button
          type="submit"
          disabled={isLoading || !query.trim()}
          className="search-button"
        >
          {isLoading ? 'Buscando...' : 'Buscar'}
        </button>
      </div>

      <div className="search-filters">
        <div className="filter-group">
          <label htmlFor="category-filter" className="filter-label">Categoría:</label>
          <select
            id="category-filter"
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value as 'all' | 'otc' | 'rx' | 'general')}
            className="filter-select"
          >
            {CATEGORIES.map(cat => (
              <option key={cat.id} value={cat.value}>{cat.label}</option>
            ))}
          </select>
        </div>

        <div className="filter-group">
          <label htmlFor="radius-filter" className="filter-label">Radio (km):</label>
          <select
            id="radius-filter"
            value={radius}
            onChange={(e) => setRadius(Number(e.target.value))}
            className="filter-select"
          >
            {[5, 10, 15, 20, 25, 50].map(r => (
              <option key={r} value={r}>{r} km</option>
            ))}
          </select>
        </div>

        <div className="filter-group location-group">
          <button
            type="button"
            onClick={handleGeolocation}
            disabled={isLoading || useGeolocation}
            className={`location-button ${useGeolocation ? 'active' : ''} ${locationError ? 'error' : ''}`}
          >
            {useGeolocation ? '📍 Ubicación activa' : '📍 Usar mi ubicación'}
          </button>
          {locationError && <span className="location-error">{locationError}</span>}
          {userLocation && !useGeolocation && (
            <span className="default-location">📍 Villahermosa (por defecto)</span>
          )}
        </div>
      </div>

      {/* Aviso informativo para medicamentos con receta */}
      {selectedCategory === 'rx' && (
        <div className="rx-warning" role="alert">
          ⚠️ <strong>Información:</strong> Los medicamentos con receta requieren prescripción médica.
          Los precios mostrados son referenciales. Consulte a su médico antes de adquirir cualquier medicamento.
        </div>
      )}
    </form>
  );
}