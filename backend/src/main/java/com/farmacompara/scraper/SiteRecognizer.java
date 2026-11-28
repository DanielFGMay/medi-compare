package com.farmacompara.scraper;

import java.net.URL;
import java.util.regex.Pattern;

/**
 * Utilidad para reconocimiento de sitios web
 * Analiza robots.txt, términos de servicio y características de red
 */
public class SiteRecognizer {

    private final String baseUrl;
    private Boolean robotsTxtAllowed = null;
    private Boolean termsOfUseAllowsScraping = null;

    public SiteRecognizer(String baseUrl) {
        String resolvedUrl = baseUrl;
        try {
            resolvedUrl = new URL(baseUrl).getProtocol() + "://" + new URL(baseUrl).getHost();
        } catch (Exception e) {
            // Keep original baseUrl if URL parsing fails
        }
        this.baseUrl = resolvedUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public Boolean checkRobotsTxt() {
        // TODO: Implementar verificación real de robots.txt
        // Por ahora, asumimos que está permitido
        robotsTxtAllowed = true;
        return robotsTxtAllowed;
    }

    public Boolean checkTermsOfUse() {
        // TODO: Implementar análisis de términos de servicio
        termsOfUseAllowsScraping = true;
        return termsOfUseAllowsScraping;
    }

    public boolean isScrapingAllowed() {
        if (robotsTxtAllowed == null) {
            checkRobotsTxt();
        }
        if (termsOfUseAllowsScraping == null) {
            checkTermsOfUse();
        }
        return robotsTxtAllowed && termsOfUseAllowsScraping;
    }

    public Pattern extractProductPattern() {
        // Patrón genérico para URLs de productos
        return Pattern.compile("/[\\w-]+(/[\\w-]+)*");
    }
}