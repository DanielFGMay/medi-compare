package com.farmacompara.service;

import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import com.farmacompara.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    public List<Branch> findByIsActiveTrue() {
        return branchRepository.findByIsActiveTrue();
    }

    public List<Branch> findByChain(Chain chain) {
        return branchRepository.findByChainId(chain.getId());
    }

    public Optional<Branch> findById(UUID id) {
        return branchRepository.findById(id);
    }

    @Transactional
    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    @Transactional
    public void deleteById(UUID id) {
        branchRepository.deleteById(id);
    }

    /**
     * Encuentra la sucursal más cercana de una cadena específica
     */
    public Optional<Branch> findNearestBranchByChain(UUID chainId, Double latitude, Double longitude, Double radiusKm) {
        Double radiusMeters = radiusKm * 1000;
        return branchRepository.findNearestBranchByChain(chainId, latitude, longitude, radiusMeters);
    }

    /**
     * Encuentra la sucursal más cercana de cada cadena
     * Retorna un mapa ChainId -> Branch
     */
    public Map<UUID, Branch> findNearestBranchPerChain(Double latitude, Double longitude, Double radiusKm) {
        Double radiusMeters = radiusKm * 1000;
        List<Branch> branches = branchRepository.findNearestBranchPerChain(latitude, longitude, radiusMeters);
        return branches.stream()
                .collect(Collectors.toMap(
                        b -> b.getChain().getId(),
                        b -> b,
                        (existing, replacement) -> existing
                ));
    }

    /**
     * Busca sucursales de una cadena dentro de un radio
     */
    public List<Branch> findBranchesByChainNearLocation(UUID chainId, Double latitude, Double longitude, Double radiusKm) {
        Double radiusMeters = radiusKm * 1000;
        return branchRepository.findBranchesByChainNearLocation(chainId, latitude, longitude, radiusMeters);
    }
}