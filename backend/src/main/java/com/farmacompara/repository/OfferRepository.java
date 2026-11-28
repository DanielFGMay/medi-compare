package com.farmacompara.repository;

import com.farmacompara.model.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferRepository extends JpaRepository<Offer, UUID> {

    List<Offer> findByProductId(UUID productId);

    List<Offer> findByChainId(UUID chainId);

    List<Offer> findByBranchId(UUID branchId);

    List<Offer> findByProductIdAndChainId(UUID productId, UUID chainId);

    @Query("SELECT o FROM Offer o WHERE o.product.id = :productId AND o.branch.id = :branchId")
    List<Offer> findByProductIdAndBranchId(@Param("productId") UUID productId, @Param("branchId") UUID branchId);

    @Query("SELECT o FROM Offer o JOIN o.product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY o.price ASC")
    List<Offer> searchOffersByProduct(@Param("name") String name);

    @Query("SELECT o FROM Offer o WHERE o.scrapedAt >= :since")
    List<Offer> findRecentOffers(@Param("since") LocalDateTime since);

    @Query("SELECT o FROM Offer o WHERE o.product.id = :productId ORDER BY o.price ASC")
    List<Offer> findByProductIdOrderByPriceAsc(@Param("productId") UUID productId);

    @Query("SELECT o FROM Offer o WHERE o.branch.id = :branchId AND o.pricePromo IS NOT NULL")
    List<Offer> findPromoOffersByBranch(@Param("branchId") UUID branchId);

    @Modifying
    @Query("DELETE FROM Offer o WHERE o.scrapedAt < :before")
    void deleteOldOffers(@Param("before") LocalDateTime before);

    /**
     * Encuentra la mejor oferta (precio más bajo) por cadena para un producto
     * Considera precio promocional si existe
     */
    @Query("""
        SELECT o FROM Offer o
        WHERE o.product.id = :productId
          AND o.chain.id IN :chainIds
          AND o.price IS NOT NULL
        ORDER BY o.chain.id, COALESCE(o.pricePromo, o.price) ASC
        """)
    List<Offer> findBestOfferPerChainForProduct(
            @Param("productId") UUID productId,
            @Param("chainIds") List<UUID> chainIds
    );

    /**
     * Encuentra la oferta más reciente por cadena para un producto
     * (para mostrar la fecha de última consulta)
     * USANDO CONSULTA NATIVA PORQUE DISTINCT ON NO FUNCIONA EN HQL
     */
    @Query(value = """
        SELECT * FROM offer o
        WHERE o.product_id = :productId
          AND o.chain_id IN (:chainIds)
        ORDER BY o.chain_id, o.scraped_at DESC
        """, nativeQuery = true)
    List<Offer> findLatestOfferPerChainForProductNative(
            @Param("productId") UUID productId,
            @Param("chainIds") List<UUID> chainIds
    );
}