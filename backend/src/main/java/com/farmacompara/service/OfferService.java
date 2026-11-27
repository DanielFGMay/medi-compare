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
import java.util.UUID;

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
}