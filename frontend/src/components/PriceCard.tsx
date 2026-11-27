import { OfferComparison, NearestBranch } from '../types/api';

interface PriceCardProps {
  offer: OfferComparison;
  isBest: boolean;
  onVisitStore: (url: string) => void;
}

export function PriceCard({ offer, isBest, onVisitStore }: PriceCardProps) {
  const formatPrice = (price: number): string => {
    return new Intl.NumberFormat('es-MX', {
      style: 'currency',
      currency: 'MXN',
      minimumFractionDigits: 2,
    }).format(price);
  };

  const formatDate = (dateString: string): string => {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString('es-MX', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      });
    } catch {
      return 'Fecha no disponible';
    }
  };

  const calculateDistance = (branch: NearestBranch | undefined): string => {
    if (!branch || branch.distanceKm === undefined) return 'Distancia no disponible';
    if (branch.distanceKm < 1) {
      return `${Math.round(branch.distanceKm * 1000)} m`;
    }
    return `${branch.distanceKm.toFixed(1)} km`;
  };

  const hasPromo = offer.pricePromo !== undefined && offer.pricePromo !== null && offer.pricePromo < offer.price;

  return (
    <article className={`price-card ${isBest ? 'best-price' : ''}`}>
      {isBest && <div className="best-badge">🏆 Mejor precio</div>}

      <div className="card-header">
        <div className="chain-info">
          {offer.chainLogoUrl && (
            <img
              src={offer.chainLogoUrl}
              alt={offer.chainName}
              className="chain-logo"
              onError={(e) => { e.currentTarget.style.display = 'none'; }}
            />
          )}
          <span className="chain-name">{offer.chainName}</span>
        </div>
      </div>

      <div className="card-prices">
        {hasPromo && (
          <div className="price-row promo-row">
            <span className="price-label">Precio regular:</span>
            <span className="price-original">{formatPrice(offer.price)}</span>
          </div>
        )}
        <div className="price-row effective-price">
          <span className="price-label">{hasPromo ? 'Precio con promo:' : 'Precio:'}</span>
          <span className="price-value">{formatPrice(offer.effectivePrice)}</span>
        </div>
        {offer.promoText && (
          <div className="promo-text">🏷️ {offer.promoText}</div>
        )}
      </div>

      <div className="card-meta">
        <div className="meta-item">
          <span className="meta-label">Última consulta:</span>
          <span className="meta-value">{formatDate(offer.lastScrapedAt)}</span>
        </div>
        {offer.nearestBranch && (
          <div className="meta-item">
            <span className="meta-label">Sucursal más cercana:</span>
            <span className="meta-value branch-name">{offer.nearestBranch.name}</span>
          </div>
        )}
        {offer.nearestBranch && offer.nearestBranch.address && (
          <div className="meta-item">
            <span className="meta-label">Dirección:</span>
            <span className="meta-value branch-address">{offer.nearestBranch.address}</span>
          </div>
        )}
        {offer.nearestBranch && (
          <div className="meta-item">
            <span className="meta-label">Distancia:</span>
            <span className="meta-value distance">{calculateDistance(offer.nearestBranch)}</span>
          </div>
        )}
      </div>

      <div className="card-actions">
        {offer.productUrl && (
          <button
            onClick={() => onVisitStore(offer.productUrl!)}
            className="visit-store-button"
            aria-label={`Ir a ${offer.chainName}`}
          >
            🛒 Ir a la tienda
          </button>
        )}
      </div>
    </article>
  );
}