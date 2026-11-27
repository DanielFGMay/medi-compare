/**
 * Tipos para la API del backend
 * Actualizados para coincidir con la nueva estructura del comparador
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
  website?: string;
  isActive?: boolean;
}

export interface Branch {
  id: string;
  chainId: string;
  name: string;
  address?: string;
  latitude: number;
  longitude: number;
  isActive?: boolean;
}

export interface ProductCategory {
  id: string;
  name: string;
  code: string;
  isActive?: boolean;
}

export interface Product {
  id: string;
  name: string;
  category?: ProductCategory;
  isRx: boolean;
  strength?: string;
  form?: string;
}

export interface Offer {
  id: string;
  productId: string;
  chainId: string;
  branchId?: string;
  price: number;
  pricePromo?: number;
  promoText?: string;
  productUrl?: string;
  scrapedAt: string;
  createdAt?: string;
}

export interface UserLocation {
  latitude: number;
  longitude: number;
}

export interface NearestBranch {
  id: string;
  chainId: string;
  name: string;
  address?: string;
  latitude: number;
  longitude: number;
  distanceKm?: number;
}

export interface OfferComparison {
  chainId: string;
  chainName: string;
  chainLogoUrl?: string;
  chainWebsite?: string;
  price: number;
  pricePromo?: number;
  effectivePrice: number;
  promoText?: string;
  productUrl?: string;
  lastScrapedAt: string;
  nearestBranch?: NearestBranch;
}

export interface PriceComparison {
  productId: string;
  productName: string;
  isRx: boolean;
  offerComparisons: OfferComparison[];
  bestPrice: number;
  bestChainId: string;
  userLocation: UserLocation;
}

export interface SearchRequest {
  productName: string;
  latitude?: number;
  longitude?: number;
  radiusKm?: number;
  categoryId?: string;
  isRx?: boolean;
}

export interface CategoryFilter {
  id: string;
  label: string;
  value: 'all' | 'otc' | 'rx' | 'general';
}