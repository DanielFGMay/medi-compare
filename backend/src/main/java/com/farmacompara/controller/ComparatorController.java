package com.farmacompara.controller;

import com.farmacompara.model.Product;
import com.farmacompara.service.ProductService;
import com.farmacompara.service.OfferService;
import com.farmacompara.model.Offer;
import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import com.farmacompara.service.BranchService;
import com.farmacompara.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.math.BigDecimal;
import java.util.stream.Collectors;
import java.util.Optional;

/**
 * Controlador del comparador de precios
 * API principal del negocio: compara precios de productos entre farmacias
 */
@RestController
@RequestMapping("/api")
public class ComparatorController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private ChainService chainService;

    /**
     * Búsqueda de productos por nombre
     */
    @GetMapping("/products")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String q,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) boolean isRx
    ) {
        List<Product> products = productService.searchByName(q);

        if (categoryId != null) {
            products = products.stream()
                .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(categoryId))
                .collect(Collectors.toList());
        }

        if (isRx) {
            products = products.stream()
                .filter(Product::getIsRx)
                .collect(Collectors.toList());
        }

        return ResponseEntity.ok(products);
    }

    /**
     * Obtener comparativa de precios para un producto específico
     * Incluye sucursal más cercana por cadena usando PostGIS
     */
    @GetMapping("/compare/{productId}")
    public ResponseEntity<PriceComparison> compareProduct(
            @PathVariable UUID productId,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "10") Double radiusKm
    ) {
        Product product = productService.findById(productId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Coordenadas por defecto: Centro de Villahermosa
        Double userLat = latitude != null ? latitude : 17.9869;
        Double userLon = longitude != null ? longitude : -92.9303;

        // Obtener todas las cadenas activas
        List<Chain> chains = chainService.findAll().stream()
            .filter(Chain::getIsActive)
            .toList();

        List<UUID> chainIds = chains.stream().map(Chain::getId).toList();

        // Obtener mejor oferta por cadena
        Map<UUID, Offer> bestOffers = offerService.findBestOfferPerChainForProduct(productId, chainIds);

        // Obtener oferta más reciente por cadena (para fecha de última consulta)
        Map<UUID, Offer> latestOffers = offerService.findLatestOfferPerChainForProduct(productId, chainIds);

        // Encontrar sucursal más cercana por cadena
        Map<UUID, Branch> nearestBranches = branchService.findNearestBranchPerChain(userLat, userLon, radiusKm);

        // Construir respuesta con ofertas enriquecidas
        List<OfferComparison> offerComparisons = new ArrayList<>();
        BigDecimal bestPrice = BigDecimal.ZERO;
        UUID bestChainId = null;

        for (Chain chain : chains) {
            Offer bestOffer = bestOffers.get(chain.getId());
            Offer latestOffer = latestOffers.get(chain.getId());
            Branch nearestBranch = nearestBranches.get(chain.getId());

            if (bestOffer != null) {
                BigDecimal effectivePrice = bestOffer.getPricePromo() != null ? bestOffer.getPricePromo() : bestOffer.getPrice();

                OfferComparison oc = new OfferComparison();
                oc.setChainId(chain.getId());
                oc.setChainName(chain.getName());
                oc.setChainLogoUrl(chain.getLogoUrl());
                oc.setChainWebsite(chain.getWebsite());
                oc.setPrice(bestOffer.getPrice());
                oc.setPricePromo(bestOffer.getPricePromo());
                oc.setEffectivePrice(effectivePrice);
                oc.setPromoText(bestOffer.getPromoText());
                oc.setProductUrl(bestOffer.getProductUrl());
                oc.setLastScrapedAt(latestOffer != null ? latestOffer.getScrapedAt() : bestOffer.getScrapedAt());
                oc.setNearestBranch(nearestBranch);

                offerComparisons.add(oc);

                if (bestPrice.equals(BigDecimal.ZERO) || effectivePrice.compareTo(bestPrice) < 0) {
                    bestPrice = effectivePrice;
                    bestChainId = chain.getId();
                }
            }
        }

        if (offerComparisons.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        // Ordenar por precio efectivo ascendente
        offerComparisons.sort(Comparator.comparing(OfferComparison::getEffectivePrice));

        PriceComparison comparison = new PriceComparison();
        comparison.setProductId(productId);
        comparison.setProductName(product.getName());
        comparison.setIsRx(product.getIsRx());
        comparison.setOfferComparisons(offerComparisons);
        comparison.setBestPrice(bestPrice);
        comparison.setBestChainId(bestChainId);
        comparison.setUserLocation(new UserLocation(userLat, userLon));

        return ResponseEntity.ok(comparison);
    }

    /**
     * Comparar precios por nombre de producto con geolocalización
     */
    @GetMapping("/compare")
    public ResponseEntity<List<PriceComparison>> compareByName(
            @RequestParam String q,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false, defaultValue = "10") Double radiusKm
    ) {
        List<Product> products = productService.searchByName(q);

        // Coordenadas por defecto: Centro de Villahermosa
        Double userLat = latitude != null ? latitude : 17.9869;
        Double userLon = longitude != null ? longitude : -92.9303;

        List<PriceComparison> results = products.stream()
            .limit(20)
            .map(p -> compareProduct(p.getId(), userLat, userLon, radiusKm).getBody())
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        return ResponseEntity.ok(results);
    }

    // DTOs para respuesta
    public static class PriceComparison {
        private UUID productId;
        private String productName;
        private Boolean isRx;
        private List<OfferComparison> offerComparisons;
        private BigDecimal bestPrice;
        private UUID bestChainId;
        private UserLocation userLocation;

        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public Boolean getIsRx() { return isRx; }
        public void setIsRx(Boolean isRx) { this.isRx = isRx; }
        public List<OfferComparison> getOfferComparisons() { return offerComparisons; }
        public void setOfferComparisons(List<OfferComparison> offerComparisons) { this.offerComparisons = offerComparisons; }
        public BigDecimal getBestPrice() { return bestPrice; }
        public void setBestPrice(BigDecimal bestPrice) { this.bestPrice = bestPrice; }
        public UUID getBestChainId() { return bestChainId; }
        public void setBestChainId(UUID bestChainId) { this.bestChainId = bestChainId; }
        public UserLocation getUserLocation() { return userLocation; }
        public void setUserLocation(UserLocation userLocation) { this.userLocation = userLocation; }
    }

    public static class OfferComparison {
        private UUID chainId;
        private String chainName;
        private String chainLogoUrl;
        private String chainWebsite;
        private BigDecimal price;
        private BigDecimal pricePromo;
        private BigDecimal effectivePrice;
        private String promoText;
        private String productUrl;
        private java.time.LocalDateTime lastScrapedAt;
        private Branch nearestBranch;

        public UUID getChainId() { return chainId; }
        public void setChainId(UUID chainId) { this.chainId = chainId; }
        public String getChainName() { return chainName; }
        public void setChainName(String chainName) { this.chainName = chainName; }
        public String getChainLogoUrl() { return chainLogoUrl; }
        public void setChainLogoUrl(String chainLogoUrl) { this.chainLogoUrl = chainLogoUrl; }
        public String getChainWebsite() { return chainWebsite; }
        public void setChainWebsite(String chainWebsite) { this.chainWebsite = chainWebsite; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public BigDecimal getPricePromo() { return pricePromo; }
        public void setPricePromo(BigDecimal pricePromo) { this.pricePromo = pricePromo; }
        public BigDecimal getEffectivePrice() { return effectivePrice; }
        public void setEffectivePrice(BigDecimal effectivePrice) { this.effectivePrice = effectivePrice; }
        public String getPromoText() { return promoText; }
        public void setPromoText(String promoText) { this.promoText = promoText; }
        public String getProductUrl() { return productUrl; }
        public void setProductUrl(String productUrl) { this.productUrl = productUrl; }
        public java.time.LocalDateTime getLastScrapedAt() { return lastScrapedAt; }
        public void setLastScrapedAt(java.time.LocalDateTime lastScrapedAt) { this.lastScrapedAt = lastScrapedAt; }
        public Branch getNearestBranch() { return nearestBranch; }
        public void setNearestBranch(Branch nearestBranch) { this.nearestBranch = nearestBranch; }
    }

    public static class UserLocation {
        private Double latitude;
        private Double longitude;

        public UserLocation() {}
        public UserLocation(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }

    }