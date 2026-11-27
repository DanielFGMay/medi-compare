import { PriceComparison } from '../types/api';
import { PriceCard } from './PriceCard';

interface ComparisonResultsProps {
  results: PriceComparison[] | null;
  isLoading: boolean;
  error: string | null;
  onVisitStore: (url: string) => void;
}

export function ComparisonResults({ results, isLoading, error, onVisitStore }: ComparisonResultsProps) {
  if (isLoading) {
    return (
      <div className="results-loading" role="status" aria-live="polite">
        <div className="spinner"></div>
        <p>Buscando precios en farmacias...</p>
      </div>
    );
  }

  if (error) {
    return (
      <div className="results-error" role="alert">
        <p>❌ {error}</p>
      </div>
    );
  }

  if (!results || results.length === 0) {
    return (
      <div className="results-empty">
        <p>🔍 No se encontraron resultados para tu búsqueda.</p>
        <p className="hint">Intenta con otro nombre de medicamento o ajusta los filtros.</p>
      </div>
    );
  }

  return (
    <div className="comparison-results" role="region" aria-label="Resultados de comparación de precios">
      <div className="results-header">
        <h2>Resultados para <strong>{results.length}</strong> producto{results.length !== 1 ? 's' : ''}</h2>
        {results[0]?.userLocation && (
          <p className="location-info">
            📍 Ubicación: {results[0].userLocation.latitude.toFixed(4)}, {results[0].userLocation.longitude.toFixed(4)}
            {results[0].userLocation.latitude === 17.9869 && results[0].userLocation.longitude === -92.9303 && ' (Villahermosa por defecto)'}
          </p>
        )}
      </div>

      {results.map((comparison) => (
        <section key={comparison.productId} className="product-comparison" aria-labelledby={`product-${comparison.productId}`}>
          <header className="product-header">
            <h3 id={`product-${comparison.productId}`} className="product-name">
              {comparison.productName}
            </h3>
            {comparison.isRx && (
              <span className="rx-badge" title="Requiere receta médica">💊 Con receta</span>
            )}
          </header>

          <div className="price-cards-grid" role="list">
            {comparison.offerComparisons.map((offer) => (
              <PriceCard
                key={`${comparison.productId}-${offer.chainId}`}
                offer={offer}
                isBest={offer.chainId === comparison.bestChainId}
                onVisitStore={onVisitStore}
              />
            ))}
          </div>

          {comparison.offerComparisons.length === 0 && (
            <p className="no-offers">No hay precios disponibles para este producto en este momento.</p>
          )}
        </section>
      ))}
    </div>
  );
}