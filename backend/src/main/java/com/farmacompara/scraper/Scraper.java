package com.farmacompara.scraper;

import com.farmacompara.model.Product;
import com.farmacompara.model.Offer;
import com.farmacompara.model.Chain;
import com.farmacompara.model.Branch;
import java.util.List;
import java.util.Set;

/**
 * Interfaz base para scrapers de precios
 */
public interface Scraper {

    /**
     * Determina si este scraper puede procesar la cadena dada
     */
    boolean supports(Chain chain);

    /**
     * Extrae productos de la cadena
     */
    List<Product> extractProducts(Chain chain, int limit);

    /**
     * Extrae ofertas para un producto específico
     */
    List<Offer> extractOffers(Product product, Chain chain, int limit);

    /**
     * Obtiene el reconocedor del sitio para análisis de robots.txt, etc.
     */
    SiteRecognizer getSiteRecognizer();

    /**
     * Nombre del scraper para logging
     */
    String getName();
}