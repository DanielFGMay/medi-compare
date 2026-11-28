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
 * Scraper para Farmacias Similares
 * https://www.farmacias-sisimilares.com
 * (Nota: el dominio real puede variar, ajustar según inspección)
 */
@Component
public class FarmaciaSimilaresScraper extends BaseScraper {

    private static final String BASE_URL = "https://www.farmacias-sisimilares.com";
    private static final String SEARCH_URL = BASE_URL + "/buscar";

    public FarmaciaSimilaresScraper() {
        super(BASE_URL);
    }

    @Override
    public boolean supports(Chain chain) {
        return chain.getName() != null &&
               chain.getName().toLowerCase().contains("similares");
    }

    @Override
    public String getName() {
        return "FarmaciaSimilaresScraper";
    }

    @Override
    protected String getProductLinkSelector() {
        return "a.product-link, .product-item a, .product-card a, .product a, .item a";
    }

    @Override
    protected String getOfferSelector() {
        return ".product-item, .product-card, .item-product, .product-grid-item, .product-list-item";
    }

    @Override
    protected Product parseProduct(Document doc, Chain chain) {
        Product product = new Product();

        Element nameElement = doc.selectFirst("h1.product-title, .product-name h1, h1.page-title, .product-detail-name, .product-info h1");
        if (nameElement == null) {
            nameElement = doc.selectFirst("[data-product-name], .product-header h1");
        }
        if (nameElement != null) {
            product.setName(cleanProductName(nameElement.text()));
        }

        Element rxBadge = doc.selectFirst(".rx-badge, .controlled-medicine, .receta-requerida, [data-rx='true'], .medicamento-controlado");
        product.setIsRx(rxBadge != null);

        Element strengthElement = doc.selectFirst(".strength, .concentration, .product-strength, .forma-farmaceutica, .contenido");
        if (strengthElement != null) {
            product.setStrength(strengthElement.text().trim());
        }

        Element formElement = doc.selectFirst(".form, .pharmaceutical-form, .presentation, .forma");
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
        Element priceElement = offerElement.selectFirst(".price-final, .price-regular, .product-price .price, .special-price, .price-box .price, .price-container .price, .precio-actual");
        if (priceElement == null) {
            priceElement = offerElement.selectFirst("[data-price], .price-wrapper .price, .precio");
        }
        if (priceElement != null) {
            BigDecimal price = extractPrice(priceElement);
            if (price != null) {
                offer.setPrice(price);
            }
        }

        // Precio promocional
        Element promoPriceElement = offerElement.selectFirst(".price-old, .price-before, .old-price, .price-promotion, .discount-price, .promo-price, .precio-anterior");
        if (promoPriceElement != null) {
            BigDecimal promoPrice = extractPrice(promoPriceElement);
            if (promoPrice != null && promoPrice.compareTo(offer.getPrice()) < 0) {
                offer.setPricePromo(promoPrice);
            }
        }

        // Texto de promoción
        Element promoTextElement = offerElement.selectFirst(".promotion-text, .discount-label, .offer-badge, .promo-tag, .discount-percent, .etiqueta-oferta");
        if (promoTextElement != null) {
            offer.setPromoText(promoTextElement.text().trim());
        }

        // URL del producto
        Element linkElement = offerElement.selectFirst("a[href*('/producto/')], a[href*('/product/')], a[href*('/p/')], .product-link, a.product-item-link");
        if (linkElement == null) {
            linkElement = offerElement.selectFirst("a");
        }
        if (linkElement != null) {
            String url = linkElement.absUrl("href");
            if (!url.isEmpty() && (url.startsWith(BASE_URL) || url.startsWith("https://farmacias"))) {
                offer.setProductUrl(url);
            }
        }

        return offer.getPrice() != null ? offer : null;
    }

    @Override
    public List<Product> extractProducts(Chain chain, int limit) {
        List<Product> products = new ArrayList<>();

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
                        if (productUrl.isEmpty()) continue;

                        Thread.sleep(2000);

                        Document productDoc = Jsoup.connect(productUrl)
                                .timeout(30000)
                                .userAgent("Farmacompara/1.0 (+https://farmacompara.com)")
                                .get();

                        Product product = parseProduct(productDoc, chain);
                        if (product != null && product.getName() != null) {
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
        return name.replaceAll("\\s+", " ").trim();
    }

    @Override
    protected BigDecimal extractPrice(Element element) {
        String priceText = element.text();
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