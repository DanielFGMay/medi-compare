package com.farmacompara.repository;

import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {

    List<Branch> findByChainId(UUID chainId);

    List<Branch> findByIsActiveTrue();

    /**
     * Encuentra la sucursal más cercana de una cadena dada usando PostGIS ST_Distance
     * La consulta usa geography para cálculo preciso en metros
     * Usa CAST(... AS geography) en lugar de ::geography para evitar que Spring interprete :: como parámetros
     */
    @Query(value = """
        SELECT b.* FROM branch b
        WHERE b.chain_id = :chainId
          AND b.is_active = true
          AND b.latitude IS NOT NULL
          AND b.longitude IS NOT NULL
          AND ST_DWithin(
              CAST(ST_MakePoint(b.longitude, b.latitude) AS geography),
              CAST(ST_MakePoint(:longitude, :latitude) AS geography),
              :radiusMeters
          )
        ORDER BY ST_Distance(
            CAST(ST_MakePoint(b.longitude, b.latitude) AS geography),
            CAST(ST_MakePoint(:longitude, :latitude) AS geography)
        )
        LIMIT 1
        """, nativeQuery = true)
    Optional<Branch> findNearestBranchByChain(
            @Param("chainId") UUID chainId,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );

    /**
     * Encuentra la sucursal más cercana de cada cadena usando PostGIS
     * Retorna una sucursal por cadena (la más cercana)
     * Usa CAST(... AS geography) en lugar de ::geography para evitar que Spring interprete :: como parámetros
     */
    @Query(value = """
        SELECT DISTINCT ON (b.chain_id) b.* FROM branch b
        WHERE b.is_active = true
          AND b.latitude IS NOT NULL
          AND b.longitude IS NOT NULL
          AND ST_DWithin(
              CAST(ST_MakePoint(b.longitude, b.latitude) AS geography),
              CAST(ST_MakePoint(:longitude, :latitude) AS geography),
              :radiusMeters
          )
        ORDER BY b.chain_id, ST_Distance(
            CAST(ST_MakePoint(b.longitude, b.latitude) AS geography),
            CAST(ST_MakePoint(:longitude, :latitude) AS geography)
        )
        """, nativeQuery = true)
    List<Branch> findNearestBranchPerChain(
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );

    /**
     * Busca sucursales dentro de un radio (para fallback o listado)
     * Usa CAST(... AS geography) en lugar de ::geography para evitar que Spring interprete :: como parámetros
     */
    @Query(value = """
        SELECT b.* FROM branch b
        WHERE b.chain_id = :chainId
          AND b.is_active = true
          AND b.latitude IS NOT NULL
          AND b.longitude IS NOT NULL
          AND ST_DWithin(
              CAST(ST_MakePoint(b.longitude, b.latitude) AS geography),
              CAST(ST_MakePoint(:longitude, :latitude) AS geography),
              :radiusMeters
          )
        ORDER BY ST_Distance(
            CAST(ST_MakePoint(b.longitude, b.latitude) AS geography),
            CAST(ST_MakePoint(:longitude, :latitude) AS geography)
        )
        """, nativeQuery = true)
    List<Branch> findBranchesByChainNearLocation(
            @Param("chainId") UUID chainId,
            @Param("latitude") Double latitude,
            @Param("longitude") Double longitude,
            @Param("radiusMeters") Double radiusMeters
    );
}