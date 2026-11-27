/**
 * Tipos para la API del backend
 * Definidos para futura integración con el comparador de precios
 */

export interface HealthResponse {
  status: string;
  timestamp: string;
  service: string;
}

export interface Chain {
  id: string;
  name: string;
  logoUrl?: string;
}

export interface Branch {
  id: string;
  chainId: string;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
}

export interface ProductCategory {
  id: string;
  name: string;
  code: string;
}

export interface Product {
  id: string;
  name: string;
  category: string;
  isRx: boolean;
}

export interface Offer {
  id: string;
  productId: string;
  chainId: string;
  price: number;
  pricePromo?: number;
  promoText?: string;
  productUrl: string;
  scrapedAt: string;
  branchId?: string;
}

export interface SearchRequest {
  productName: string;
  latitude?: number;
  longitude?: number;
  radiusKm?: number;
  categoryId?: string;
}

export interface PriceComparison {
  productId: string;
  productName: string;
  offers: Offer[];
  bestPrice: number;
  bestChainId: string;
  closestBranch: Branch;
}