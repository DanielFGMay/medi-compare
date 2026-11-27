package com.farmacompara.scraper;

import com.farmacompara.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Scraper para Farmacias Guadalajara
 * https://www.farmaciasguadalajara.com
 * Estructura típica: búsqueda por nombre de producto, lista de resultados con precios
 */
@Component
public class FarmaciaGuadalajaraScraper extends BaseScraper {

    private static final String BASE_URL = "https://www.farmaciasguadalajara.com";
    private static final String SEARCH_URL = BASE_URL + "/search";

    public FarmaciaGuadalajaraScraper() {
        super(BASE_URL);
    }

    @Override
    public boolean supports(Chain chain) {
        return chain.getName() != null &&
               chain.getName().toLowerCase().contains("guadalajara");
    }

    @Override
    public String getName() {
        return "FarmaciaGuadalajaraScraper";
    }

    @Override
    protected String getProductLinkSelector() {
        // Selector para enlaces de productos en resultados de búsqueda
        return "a.product-item-link, .product-item a, .product-card a, .item-product a";
    }

    @Override
    protected String getOfferSelector() {
        // Selector para elementos de oferta/precio
        return ".product-item, .product-card, .item-product, .product-price-container";
    }

    @Override
    protected Product parseProduct(Document doc, Chain chain) {
        Product product = new Product();

        // Nombre del producto - varios selectores posibles
        Element nameElement = doc.selectFirst("h1.product-title, .product-name h1, h1.page-title, .product-detail-name");
        if (nameElement == null) {
            nameElement = doc.selectFirst("[data-testid='product-name'], .product-info h1");
        }
        if (nameElement != null) {
            product.setName(cleanProductName(nameElement.text()));
        }

        // Categoría
        Element categoryElement = doc.selectFirst(".product-category, .breadcrumb li:last-child, .category-path");
        if (categoryElement != null) {
            // La categoría se asigna por el servicio
        }

        // Verificar si requiere receta (medicamento controlado)
        Element rxBadge = doc.selectFirst(".rx-badge, .controlled-medicine, .receta-requerida, [data-rx='true']");
        product.setIsRx(rxBadge != null);

        // Concentración y forma
        Element strengthElement = doc.selectFirst(".strength, .concentration, .product-strength, .forma-farmaceutica");
        if (strengthElement != null) {
            product.setStrength(strengthElement.text().trim());
        }

        Element formElement = doc.selectFirst(".form, .pharmaceutical-form, .presentation");
        if (formElement != null) {
            product.setForm(formElement.text().trim());
        }

        return product.getName() != null ? product : null;
    }

    @Override
    protected Offer parseOffer(Element offerElement, Product product, Chain chain) {
        Offer offer = new Offer();
        offer.setProduct(product);
        offer.setChain(chain);

        // Precio regular
        Element priceElement = offerElement.selectFirst(".price-final, .price-regular, .product-price .price, .special-price, .price-box .price");
        if (priceElement == null) {
            priceElement = offerElement.selectFirst("[data-price], .price-container .price");
        }
        if (priceElement != null) {
            BigDecimal price = extractPrice(priceElement);
            if (price != null) {
                offer.setPrice(price);
            }
        }

        // Precio promocional / oferta
        Element promoPriceElement = offerElement.selectFirst(".price-old, .price-before, .old-price, .price-promotion, .discount-price");
        if (promoPriceElement != null) {
            BigDecimal promoPrice = extractPrice(promoPriceElement);
            if (promoPrice != null && promoPrice.compareTo(offer.getPrice()) < 0) {
                offer.setPricePromo(promoPrice);
            }
        }

        // Texto de promoción
        Element promoTextElement = offerElement.selectFirst(".promotion-text, .discount-label, .offer-badge, .promo-tag");
        if (promoTextElement != null) {
            offer.setPromoText(promoTextElement.text().trim());
        }

        // URL del producto
        Element linkElement = offerElement.selectFirst("a[href*='/product/'], a[href*='/p/'], .product-link");
        if (linkElement == null) {
            linkElement = offerElement.selectFirst("a");
        }
        if (linkElement != null) {
            String url = linkElement.absUrl("href");
            if (!url.isEmpty() && url.startsWith(BASE_URL)) {
                offer.setProductUrl(url);
            }
        }

        // SKU / código de producto si está disponible
        Element skuElement = offerElement.selectFirst("[data-sku], .sku, .product-code");
        // Podríamos guardar esto en el producto si fuera necesario

        return offer.getPrice() != null ? offer : null;
    }

    /**
     * Busca productos por nombre en Farmacias Guadalajara
     * Sobrescribe el método base para usar búsqueda real
     */
    @Override
    public List<Product> extractProducts(Chain chain, int limit) {
        List<Product> products = new ArrayList<>();

        // Lista de medicamentos/productos objetivo para la búsqueda inicial
        String[] searchTerms = {
            "paracetamol", "ibuprofeno", "aspirina", "amoxicilina", "loratadina",
            "omeprazol", "metformina", "losartan", "atorvastatina", "levotiroxina",
            "vitamina c", "vitamina d", "complejo b", "probioticos",
            "gel antibacterial", "cubrebocas", "alcohol", "agua oxigenada"
        };

        for (String term : searchTerms) {
            if (products.size() >= limit) break;

            try {
                String searchUrl = SEARCH_URL + "?q=" + java.net.URLEncoder.encode(term, "UTF-8");
                Document doc = Jsoup.connect(searchUrl)
                        .timeout(30000)
                        .userAgent("Farmacompara/1.0 (+https://farmacompara.com)")
                        .get();

                Elements productLinks = doc.select(getProductLinkSelector());

                for (Element link : productLinks) {
                    if (products.size() >= limit) break;

                    try {
                        String productUrl = link.absUrl("href");
                        if (productUrl.isEmpty() || !productUrl.startsWith(BASE_URL)) continue;

                        // Pequeña pausa para ser respetuoso
                        Thread.sleep(2000);

                        Document productDoc = Jsoup.connect(productUrl)
                                .timeout(30000)
                                .userAgent("Farmacompara/1.0 (+https://farmacompara.com)")
                                .get();

                        Product product = parseProduct(productDoc, chain);
                        if (product != null && product.getName() != null) {
                            // Verificar duplicados
                            boolean exists = products.stream()
                                    .anyMatch(p -> p.getName().equalsIgnoreCase(product.getName()));
                            if (!exists) {
                                products.add(product);
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("Error parsing product from {}: {}", link.attr("href"), e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.error("Error searching for '{}': {}", term, e.getMessage());
            }
        }

        return products;
    }

    /**
     * Extrae ofertas para un producto específico buscando en la web
     */
    @Override
    public List<Offer> extractOffers(Product product, Chain chain, int limit) {
        List<Offer> offers = new ArrayList<>();

        try {
            String searchUrl = SEARCH_URL + "?q=" + java.net.URLEncoder.encode(product.getName(), "UTF-8");
            Document doc = Jsoup.connect(searchUrl)
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

    private String cleanProductName(String name) {
        if (name == null) return null;
        // Limpiar nombre: quitar espacios extra, normalizar
        return name.replaceAll("\\s+", " ").trim();
    }

    @Override
    protected BigDecimal extractPrice(Element element) {
        String priceText = element.text();
        // Buscar patrón de precio mexicano: $123.45 o $ 123,45 o 123.45
        Pattern pricePattern = Pattern.compile("[\\$\\s]*([\\d,.]+)");
        Matcher matcher = pricePattern.matcher(priceText);
        if (matcher.find()) {
            String priceStr = matcher.group(1).replace(",", ".").replaceAll("[^0-9.]", "");
            try {
                return new BigDecimal(priceStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}