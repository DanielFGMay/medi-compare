package com.farmacompara.service;

import com.farmacompara.client.OverpassClient;
import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import com.farmacompara.repository.BranchRepository;
import com.farmacompara.repository.ChainRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para importar sucursales reales desde OpenStreetMap (Overpass API)
 * a la base de datos Neon con PostGIS
 */
@Service
public class BranchImportService {

    private static final Logger logger = LoggerFactory.getLogger(BranchImportService.class);

    @Autowired
    private OverpassClient overpassClient;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    private BranchRepository branchRepository;

    /**
     * Importa sucursales de las 3 cadenas objetivo en Villahermosa/Cunduacán
     */
    @Transactional
    public ImportResult importBranchesFromOverpass() {
        ImportResult result = new ImportResult();

        // 1. Obtener o crear las 3 cadenas objetivo
        Map<String, Chain> chainMap = ensureTargetChainsExist();
        result.setChainsProcessed(chainMap.size());

        // 2. Consultar Overpass API
        List<OverpassClient.OverpassElement> elements = overpassClient.fetchPharmaciesInArea();
        result.setOverpassElementsFound(elements.size());

        if (elements.isEmpty()) {
            result.addMessage("No se encontraron elementos en Overpass para el área objetivo");
            return result;
        }

        // 3. Convertir a entidades Branch
        List<Branch> newBranches = overpassClient.convertToBranches(elements, chainMap);
        result.setBranchesConverted(newBranches.size());

        if (newBranches.isEmpty()) {
            result.addMessage("No se pudieron convertir sucursales válidas");
            return result;
        }

        // 4. Guardar en BD (evitando duplicados por coordenadas y cadena)
        int saved = 0;
        int skipped = 0;

        for (Branch branch : newBranches) {
            // Verificar si ya existe una sucursal muy cercana de la misma cadena
            List<Branch> existingNearby = branchRepository.findByChainId(branch.getChain().getId());
            boolean exists = existingNearby.stream().anyMatch(existing ->
                    calculateDistance(existing.getLatitude(), existing.getLongitude(),
                            branch.getLatitude(), branch.getLongitude()) < 0.05 // ~50 metros
            );

            if (exists) {
                skipped++;
                logger.debug("Sucursal duplicada omitida: {} ({})", branch.getName(), branch.getChain().getName());
            } else {
                branchRepository.save(branch);
                saved++;
            }
        }

        result.setBranchesSaved(saved);
        result.setBranchesSkipped(skipped);
        result.addMessage(String.format("Importación completada: %d guardadas, %d omitidas (duplicadas)", saved, skipped));

        logger.info("Importación de sucursales finalizada: {}", result.getSummary());
        return result;
    }

    /**
     * Asegura que existan las 3 cadenas objetivo en la BD
     */
    private Map<String, Chain> ensureTargetChainsExist() {
        String[] targetChains = {
            "Farmacias Guadalajara",
            "Farmacias del Ahorro",
            "Farmacias Similares"
        };

        Map<String, Chain> chainMap = new HashMap<>();

        for (String name : targetChains) {
            Chain chain = chainRepository.findByName(name).orElseGet(() -> {
                Chain newChain = new Chain();
                newChain.setName(name);
                newChain.setIsActive(true);
                // URLs oficiales aproximadas
                switch (name) {
                    case "Farmacias Guadalajara":
                        newChain.setWebsite("https://www.farmaciasguadalajara.com");
                        newChain.setLogoUrl("https://www.farmaciasguadalajara.com/favicon.ico");
                        break;
                    case "Farmacias del Ahorro":
                        newChain.setWebsite("https://www.farmaciasdelahorro.com.mx");
                        newChain.setLogoUrl("https://www.farmaciasdelahorro.com.mx/favicon.ico");
                        break;
                    case "Farmacias Similares":
                        newChain.setWebsite("https://www.farmacias-sisimilares.com");
                        newChain.setLogoUrl("https://www.farmacias-sisimilares.com/favicon.ico");
                        break;
                }
                return chainRepository.save(newChain);
            });
            chainMap.put(name, chain);
        }

        return chainMap;
    }

    /**
     * Calcula distancia aproximada en km entre dos puntos (Haversine simplificado)
     */
    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return Double.MAX_VALUE;
        }

        final int R = 6371; // Radio de la Tierra en km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Resultado de la importación
     */
    public static class ImportResult {
        private int chainsProcessed = 0;
        private int overpassElementsFound = 0;
        private int branchesConverted = 0;
        private int branchesSaved = 0;
        private int branchesSkipped = 0;
        private final List<String> messages = new ArrayList<>();

        public int getChainsProcessed() { return chainsProcessed; }
        public void setChainsProcessed(int chainsProcessed) { this.chainsProcessed = chainsProcessed; }
        public int getOverpassElementsFound() { return overpassElementsFound; }
        public void setOverpassElementsFound(int overpassElementsFound) { this.overpassElementsFound = overpassElementsFound; }
        public int getBranchesConverted() { return branchesConverted; }
        public void setBranchesConverted(int branchesConverted) { this.branchesConverted = branchesConverted; }
        public int getBranchesSaved() { return branchesSaved; }
        public void setBranchesSaved(int branchesSaved) { this.branchesSaved = branchesSaved; }
        public int getBranchesSkipped() { return branchesSkipped; }
        public void setBranchesSkipped(int branchesSkipped) { this.branchesSkipped = branchesSkipped; }
        public List<String> getMessages() { return messages; }
        public void addMessage(String message) { messages.add(message); }
        public String getSummary() {
            return String.format("Cadenas: %d, Elementos Overpass: %d, Convertidos: %d, Guardados: %d, Omitidos: %d",
                    chainsProcessed, overpassElementsFound, branchesConverted, branchesSaved, branchesSkipped);
        }
    }
}