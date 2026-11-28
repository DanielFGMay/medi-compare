package com.farmacompara.controller;

import com.farmacompara.model.Chain;
import com.farmacompara.scraper.ScraperService;
import com.farmacompara.service.BranchImportService;
import com.farmacompara.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador administrativo
 * Endpoints protegidos para gestión de scraping y datos
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private ChainService chainService;

    @Autowired
    private ScraperService scraperService;

    @Autowired
    private BranchImportService branchImportService;

    /**
     * Ejecutar scraping manual para una cadena
     */
    @PostMapping("/scrape/{chainId}")
    public ResponseEntity<ScrapingResultDto> scrapeChain(@PathVariable UUID chainId) {
        ScraperService.ScrapingResult result = scraperService.scrapeChain(chainId);
        return ResponseEntity.ok(new ScrapingResultDto(result));
    }

    /**
     * Ejecutar scraping para todas las cadenas
     */
    @PostMapping("/scrape")
    public ResponseEntity<Map<String, Object>> scrapeAll() {
        Map<UUID, ScraperService.ScrapingResult> results = scraperService.scrapeAllChains();

        Map<String, Object> response = new HashMap<>();
        response.put("results", results.entrySet().stream()
            .collect(Collectors.toMap(e -> e.getKey().toString(), e -> new ScrapingResultDto(e.getValue()))));

        return ResponseEntity.ok(response);
    }

    /**
     * Obtener estado de las cadenas
     */
    @GetMapping("/chains")
    public ResponseEntity<List<Chain>> getChains() {
        return ResponseEntity.ok(chainService.findAll());
    }

    /**
     * Importar sucursales reales desde OpenStreetMap (Overpass API)
     * para Farmacias Guadalajara, Farmacias del Ahorro y Farmacias Similares
     * en Villahermosa y Cunduacán, Tabasco
     */
    @PostMapping("/import/branches")
    public ResponseEntity<BranchImportService.ImportResult> importBranchesFromOverpass() {
        BranchImportService.ImportResult result = branchImportService.importBranchesFromOverpass();
        return ResponseEntity.ok(result);
    }

    /**
     * DTO para respuesta de scraping
     */
    public static class ScrapingResultDto {
        private boolean success;
        private String message;
        private int productsScraped;
        private int offersScraped;

        public ScrapingResultDto() {}

        public ScrapingResultDto(ScraperService.ScrapingResult result) {
            this.success = result.isSuccess();
            this.message = result.getMessage();
            this.productsScraped = result.getProductsScraped();
            this.offersScraped = result.getOffersScraped();
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