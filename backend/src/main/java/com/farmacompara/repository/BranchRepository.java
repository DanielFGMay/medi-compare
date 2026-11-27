package com.farmacompara.repository;

import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {

    List<Branch> findByChainId(UUID chainId);

    List<Branch> findByIsActiveTrue();
}