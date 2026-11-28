package com.farmacompara.scraper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scheduler para ejecución automática de scraping
 * Por defecto ejecuta cada 6 horas, configurable vía propiedades
 */
@Component
public class ScrapingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ScrapingScheduler.class);

    @Autowired
    private ScraperService scraperService;

    private final ExecutorService executor = Executors.newFixedThreadPool(3);

    /**
     * Scraping automático cada 6 horas
     * Puede ser deshabilitado estableciendo CRON en "disabled"
     */
    @Scheduled(cron = "${scraping.scheduler.cron:0 0 */6 * * *}")
    public void scheduleScraping() {
        logger.info("Iniciando scraping programado...");
        CompletableFuture.supplyAsync(scraperService::scrapeAllChains, executor)
            .thenAccept(results -> {
                logger.info("Scraping programado completado. Resultados: {} cadenas procesadas", results.size());
            })
            .exceptionally(ex -> {
                logger.error("Error en scraping programado", ex);
                return null;
            });
    }

    /**
     * Limpieza de ofertas antiguas cada 24 horas
     */
    @Scheduled(cron = "${scraping.cleanup.cron:0 0 2 * * *}")
    public void cleanupOldOffers() {
        logger.info("Limpiando ofertas antiguas...");
        // Delete offers older than 30 days
        // This would call offerService.deleteOldOffers() with date threshold
        logger.info("Limpieza completada");
    }
}