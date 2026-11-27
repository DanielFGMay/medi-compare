package com.farmacompara.service;

import com.farmacompara.model.Branch;
import com.farmacompara.model.Chain;
import com.farmacompara.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
}