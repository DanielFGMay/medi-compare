package com.farmacompara.scraper;

import com.farmacompara.model.*;
import com.farmacompara.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio de scraping
 * Coordina el proceso de extracción de datos de precios
 */
@Service
public class ScraperService {

    private static final Logger logger = LoggerFactory.getLogger(ScraperService.class);

    @Autowired
    private ChainService chainService;

    @Autowired
    private BranchService branchService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OfferService offerService;

    @Autowired
    private List<Scraper> scrapers;

    /**
     * Ejecuta scraping para una cadena específica
     */
    @Transactional
    public ScrapingResult scrapeChain(UUID chainId) {
        Optional<Chain> chainOpt = chainService.findById(chainId);
        if (chainOpt.isEmpty()) {
            return new ScrapingResult(false, "Chain not found", 0, 0);
        }

        Chain chain = chainOpt.get();
        Scraper scraper = findScraper(chain);
        if (scraper == null) {
            return new ScrapingResult(false, "No scraper found for chain: " + chain.getName(), 0, 0);
        }

        int productsScraped = 0;
        int offersScraped = 0;

        try {
            List<Product> products = scraper.extractProducts(chain, 100);

            for (Product product : products) {
                Product savedProduct = productService.save(product);
                productsScraped++;

                List<Offer> offers = scraper.extractOffers(savedProduct, chain, 5);
                for (Offer offer : offers) {
                    offer.setScrapedAt(LocalDateTime.now());
                    offerService.save(offer);
                    offersScraped++;
                }
            }

            return new ScrapingResult(true, "Successfully scraped", productsScraped, offersScraped);
        } catch (Exception e) {
            logger.error("Error scraping chain {}: {}", chain.getName(), e.getMessage(), e);
            return new ScrapingResult(false, e.getMessage(), productsScraped, offersScraped);
        }
    }

    /**
     * Ejecuta scraping para todas las cadenas activas
     */
    @Transactional
    @Async
    public Map<UUID, ScrapingResult> scrapeAllChains() {
        Map<UUID, ScrapingResult> results = new HashMap<>();
        List<Chain> chains = chainService.findAll();

        for (Chain chain : chains) {
            results.put(chain.getId(), scrapeChain(chain.getId()));
        }

        return results;
    }

    private Scraper findScraper(Chain chain) {
        return scrapers.stream()
            .filter(s -> s.supports(chain))
            .findFirst()
            .orElse(null);
    }

    /**
     * Resultado de scraping
     */
    public static class ScrapingResult {
        private boolean success;
        private String message;
        private int productsScraped;
        private int offersScraped;

        public ScrapingResult() {}

        public ScrapingResult(boolean success, String message, int productsScraped, int offersScraped) {
            this.success = success;
            this.message = message;
            this.productsScraped = productsScraped;
            this.offersScraped = offersScraped;
        }

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public int getProductsScraped() { return productsScraped; }
        public void setProductsScraped(int productsScraped) { this.productsScraped = productsScraped; }

        public int getOffersScraped() { return offersScraped; }
        public void setOffersScraped(int offersScraped) { this.offersScraped = offersScraped; }
    }
}