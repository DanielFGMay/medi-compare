package com.farmacompara.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Cliente para consultar la API Overpass de OpenStreetMap
 * Importa sucursales reales de farmacias en Villahermosa y Cunduacán, Tabasco
 */
@Component
public class OverpassClient {

    private static final Logger logger = LoggerFactory.getLogger(OverpassClient.class);

    private final WebClient webClient;
    private final String overpassUrl;

    // Bounding box para Villahermosa y Cunduacán, Tabasco
    // Villahermosa: approx 17.98, -92.92 | Cunduacán: approx 18.07, -93.17
    private static final String BBOX_VILLAHERMOSA = "17.90,-93.00,18.10,-92.80";

    // Cadenas objetivo con sus tags de OSM
    private static final Map<String, String> PHARMACY_CHAINS = Map.of(
        "Farmacias Guadalajara", "Farmacias Guadalajara",
        "Farmacias del Ahorro", "Farmacias del Ahorro",
        "Farmacias Similares", "Farmacias Similares"
    );

    public OverpassClient(
            WebClient.Builder webClientBuilder,
            @Value("${overpass.url:https://overpass-api.de/api/interpreter}") String overpassUrl
    ) {
        this.webClient = webClientBuilder
                .baseUrl(overpassUrl)
                .defaultHeader("User-Agent", "Farmacompara/1.0 (+https://farmacompara.com)")
                .build();
        this.overpassUrl = overpassUrl;
    }

    /**
     * Consulta Overpass para obtener farmacias en el área de Villahermosa/Cunduacán
     */
    public List<OverpassElement> fetchPharmaciesInArea() {
        String query = buildPharmacyQuery();
        logger.info("Ejecutando consulta Overpass para farmacias en Villahermosa/Cunduacán");

        try {
            OverpassResponse response = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/interpreter")
                            .queryParam("data", query)
                            .build())
                    .retrieve()
                    .bodyToMono(OverpassResponse.class)
                    .block();

            if (response != null && response.getElements() != null) {
                logger.info("Overpass retornó {} elementos", response.getElements().size());
                return response.getElements();
            }
        } catch (Exception e) {
            logger.error("Error consultando Overpass API: {}", e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    /**
     * Construye la query Overpass QL para buscar farmacias de las cadenas objetivo
     */
    private String buildPharmacyQuery() {
        StringBuilder query = new StringBuilder();
        query.append("[out:json][timeout:60];\n");
        query.append("(");

        // Buscar por nombre de cadena en el área
        for (String chainName : PHARMACY_CHAINS.values()) {
            query.append("node[\"name\"=\"").append(escapeForOverpass(chainName)).append("\"][\"shop\"=\"pharmacy\"](").append(BBOX_VILLAHERMOSA).append(");\n");
            query.append("way[\"name\"=\"").append(escapeForOverpass(chainName)).append("\"][\"shop\"=\"pharmacy\"](").append(BBOX_VILLAHERMOSA).append(");\n");
            query.append("relation[\"name\"=\"").append(escapeForOverpass(chainName)).append("\"][\"shop\"=\"pharmacy\"](").append(BBOX_VILLAHERMOSA).append(");\n");
        }

        // También buscar por brand
        query.append("node[\"brand\"~\"Guadalajara|Ahorro|Similares\",i][\"shop\"=\"pharmacy\"](").append(BBOX_VILLAHERMOSA).append(");\n");
        query.append("way[\"brand\"~\"Guadalajara|Ahorro|Similares\",i][\"shop\"=\"pharmacy\"](").append(BBOX_VILLAHERMOSA).append(");\n");

        query.append(");\n");
        query.append("out center tags;");

        return query.toString();
    }

    private String escapeForOverpass(String input) {
        return input.replace("\"", "\\\"");
    }

    /**
     * Convierte elementos de Overpass a entidades Branch
     */
    public List<Branch> convertToBranches(List<OverpassElement> elements, Map<String, Chain> chainMap) {
        List<Branch> branches = new ArrayList<>();

        for (OverpassElement element : elements) {
            if (element.getTags() == null) continue;

            String name = element.getTags().get("name");
            if (name == null) continue;

            // Determinar a qué cadena pertenece
            Chain matchedChain = findMatchingChain(name, chainMap);
            if (matchedChain == null) continue;

            Double lat = element.getLat();
            Double lon = element.getLon();

            // Para ways/relations, usar center
            if (lat == null && element.getCenter() != null) {
                lat = element.getCenter().getLat();
                lon = element.getCenter().getLon();
            }

            if (lat == null || lon == null) {
                logger.warn("Elemento sin coordenadas: {}", name);
                continue;
            }

            // Verificar que esté en el área de interés (Villahermosa/Cunduacán)
            if (!isInTargetArea(lat, lon)) {
                continue;
            }

            Branch branch = new Branch();
            branch.setChain(matchedChain);
            branch.setName(buildBranchName(name, element.getTags()));
            branch.setAddress(buildAddress(element.getTags()));
            branch.setLatitude(lat);
            branch.setLongitude(lon);
            branch.setIsActive(true);

            branches.add(branch);
        }

        logger.info("Convertidos {} sucursales válidas", branches.size());
        return branches;
    }

    private Chain findMatchingChain(String name, Map<String, Chain> chainMap) {
        String lowerName = name.toLowerCase();

        if (lowerName.contains("guadalajara")) {
            return chainMap.get("Farmacias Guadalajara");
        } else if (lowerName.contains("ahorro")) {
            return chainMap.get("Farmacias del Ahorro");
        } else if (lowerName.contains("similares")) {
            return chainMap.get("Farmacias Similares");
        }

        // Intentar por brand
        for (Map.Entry<String, Chain> entry : chainMap.entrySet()) {
            if (lowerName.contains(entry.getKey().toLowerCase().replace("farmacias ", ""))) {
                return entry.getValue();
            }
        }

        return null;
    }

    private String buildBranchName(String name, Map<String, String> tags) {
        StringBuilder sb = new StringBuilder(name);

        // Añadir referencia si existe
        String ref = tags.get("ref");
        if (ref != null && !ref.isEmpty()) {
            sb.append(" ").append(ref);
        }

        return sb.toString();
    }

    private String buildAddress(Map<String, String> tags) {
        List<String> addressParts = new ArrayList<>();

        String street = tags.get("addr:street");
        String housenumber = tags.get("addr:housenumber");
        if (street != null) {
            addressParts.add(street + (housenumber != null ? " " + housenumber : ""));
        }

        String suburb = tags.get("addr:suburb");
        if (suburb != null) addressParts.add(suburb);

        String city = tags.get("addr:city");
        if (city != null) addressParts.add(city);

        String state = tags.get("addr:state");
        if (state != null) addressParts.add(state);

        String postcode = tags.get("addr:postcode");
        if (postcode != null) addressParts.add(postcode);

        return addressParts.isEmpty() ? null : String.join(", ", addressParts);
    }

    private boolean isInTargetArea(double lat, double lon) {
        // Villahermosa: ~17.98, -92.92 | Cunduacán: ~18.07, -93.17
        // Bounding box amplio para ambos municipios
        return lat >= 17.85 && lat <= 18.15 && lon >= -93.25 && lon <= -92.75;
    }

    // DTOs para respuesta de Overpass
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OverpassResponse {
        @JsonProperty("elements")
        private List<OverpassElement> elements;

        public List<OverpassElement> getElements() { return elements; }
        public void setElements(List<OverpassElement> elements) { this.elements = elements; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OverpassElement {
        @JsonProperty("type")
        private String type;

        @JsonProperty("id")
        private Long id;

        @JsonProperty("lat")
        private Double lat;

        @JsonProperty("lon")
        private Double lon;

        @JsonProperty("tags")
        private Map<String, String> tags;

        @JsonProperty("center")
        private Center center;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLon() { return lon; }
        public void setLon(Double lon) { this.lon = lon; }
        public Map<String, String> getTags() { return tags; }
        public void setTags(Map<String, String> tags) { this.tags = tags; }
        public Center getCenter() { return center; }
        public void setCenter(Center center) { this.center = center; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Center {
        @JsonProperty("lat")
        private Double lat;

        @JsonProperty("lon")
        private Double lon;

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLon() { return lon; }
        public void setLon(Double lon) { this.lon = lon; }
    }
}