package com.farmacompara.repository;

import com.farmacompara.model.Chain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChainRepository extends JpaRepository<Chain, UUID> {
    Optional<Chain> findByName(String name);
    boolean existsByName(String name);
}