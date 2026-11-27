package com.farmacompara.scraper;

import com.farmacompara.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Scraper base con funcionalidad común
 * Para extendido por scrapers específicos por cadena
 */
public abstract class BaseScraper implements Scraper {

    protected static final Logger logger = LoggerFactory.getLogger(BaseScraper.class);

    protected SiteRecognizer siteRecognizer;

    public BaseScraper(String baseUrl) {
        this.siteRecognizer = new SiteRecognizer(baseUrl);
    }

    @Override
    public SiteRecognizer getSiteRecognizer() {
        return siteRecognizer;
    }

    @Override
    public List<Product> extractProducts(Chain chain, int limit) {
        List<Product> products = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(getSiteRecognizer().getBaseUrl())
                    .timeout(30000)
                    .userAgent("Farmacompara/1.0 (+https://farmacompara.com)")
                    .get();

            Elements productLinks = doc.select(getProductLinkSelector());

            for (Element link : productLinks) {
                try {
                    String productUrl = link.absUrl("href");
                    if (productUrl.isEmpty()) continue;

                    Document productDoc = Jsoup.connect(productUrl)
                            .timeout(30000)
                            .userAgent("Farmacompara/1.0 (+https://farmacompara.com)")
                            .get();

                    Product product = parseProduct(productDoc, chain);
                    if (product != null && product.getName() != null) {
                        products.add(product);
                    }

                    if (products.size() >= limit) break;
                } catch (Exception e) {
                    logger.warn("Error parsing product from {}: {}", link.attr("href"), e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching products for chain {}: {}", chain.getName(), e.getMessage(), e);
        }

        return products;
    }

    @Override
    public List<Offer> extractOffers(Product product, Chain chain, int limit) {
        List<Offer> offers = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(getSiteRecognizer().getBaseUrl())
                    .timeout(30000)
                    .userAgent("Farmacompara/1.0 (+https://farmacompara.com)")
                    .get();

            Elements offerElements = doc.select(getOfferSelector());

            int count = 0;
            for (Element offerElement : offerElements) {
                if (count >= limit) break;
                try {
                    Offer offer = parseOffer(offerElement, product, chain);
                    if (offer != null) {
                        offers.add(offer);
                        count++;
                    }
                } catch (Exception e) {
                    logger.warn("Error parsing offer: {}", e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching offers for product {}: {}", product.getName(), e.getMessage(), e);
        }

        return offers;
    }

    /**
     * Parse un documento de producto en una entidad Product
     */
    protected abstract Product parseProduct(Document doc, Chain chain);

    /**
     * Parse un elemento de oferta en una entidad Offer
     */
    protected abstract Offer parseOffer(Element offerElement, Product product, Chain chain);

    /**
     * Selector CSS para enlaces de productos
     */
    protected abstract String getProductLinkSelector();

    /**
     * Selector CSS para elementos de oferta
     */
    protected abstract String getOfferSelector();

    /**
     * Extrae el precio de un elemento
     */
    protected BigDecimal extractPrice(Element element) {
        String priceText = element.text().replaceAll("[^0-9.,]", "");
        priceText = priceText.replace(",", ".").trim();
        try {
            return new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}