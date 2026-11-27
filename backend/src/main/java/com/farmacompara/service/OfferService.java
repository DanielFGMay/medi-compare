package com.farmacompara.service;

import com.farmacompara.model.Offer;
import com.farmacompara.model.Product;
import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import com.farmacompara.repository.OfferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.LinkedHashMap;

@Service
@Transactional(readOnly = true)
public class OfferService {

    @Autowired
    private OfferRepository offerRepository;

    public List<Offer> findAll() {
        return offerRepository.findAll();
    }

    public List<Offer> findByProduct(Product product) {
        return offerRepository.findByProductId(product.getId());
    }

    public List<Offer> findByChain(Chain chain) {
        return offerRepository.findByChainId(chain.getId());
    }

    public List<Offer> findByBranch(Branch branch) {
        return offerRepository.findByBranchId(branch.getId());
    }

    public List<Offer> findBestOffersForProduct(Product product) {
        return offerRepository.findByProductIdOrderByPriceAsc(product.getId());
    }

    public List<Offer> findByProductAndBranch(UUID productId, UUID branchId) {
        return offerRepository.findByProductIdAndBranchId(productId, branchId);
    }

    public List<Offer> findPromoOffersByBranch(Branch branch) {
        return offerRepository.findPromoOffersByBranch(branch.getId());
    }

    public List<Offer> searchOffersByProduct(String name) {
        return offerRepository.searchOffersByProduct(name);
    }

    @Transactional
    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    @Transactional
    public void deleteById(UUID id) {
        offerRepository.deleteById(id);
    }

    @Transactional
    public void deleteOldOffers(LocalDateTime before) {
        offerRepository.deleteOldOffers(before);
    }

    /**
     * Obtiene la mejor oferta (precio más bajo) por cada cadena para un producto
     */
    public Map<UUID, Offer> findBestOfferPerChainForProduct(UUID productId, List<UUID> chainIds) {
        List<Offer> offers = offerRepository.findBestOfferPerChainForProduct(productId, chainIds);
        return offers.stream()
                .collect(Collectors.toMap(
                        o -> o.getChain().getId(),
                        o -> o,
                        (existing, replacement) -> existing // mantener el primero (más barato)
                ));
    }

    /**
     * Obtiene la oferta más reciente por cada cadena para un producto
     * (para mostrar fecha de última consulta)
     *
     * NOTA: No podemos usar DISTINCT ON en HQL/JPQL, así que obtenemos todas las ofertas
     * y luego filtramos en Java para quedarnos con la más reciente por cadena
     */
    public Map<UUID, Offer> findLatestOfferPerChainForProduct(UUID productId, List<UUID> chainIds) {
        List<Offer> offers = offerRepository.findLatestOfferPerChainForProductNative(productId, chainIds);
        return offers.stream()
                .collect(Collectors.toMap(
                        o -> o.getChain().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing // mantener el primero (más reciente por orden)
                ));
    }
}