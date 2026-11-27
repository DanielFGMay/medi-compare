package com.farmacompara.service;

import com.farmacompara.model.Chain;
import com.farmacompara.repository.ChainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ChainService {

    @Autowired
    private ChainRepository chainRepository;

    public List<Chain> findAll() {
        return chainRepository.findAll();
    }

    public Optional<Chain> findById(UUID id) {
        return chainRepository.findById(id);
    }

    public Optional<Chain> findByName(String name) {
        return chainRepository.findByName(name);
    }

    @Transactional
    public Chain save(Chain chain) {
        return chainRepository.save(chain);
    }

    @Transactional
    public void deleteById(UUID id) {
        chainRepository.deleteById(id);
    }

    public boolean existsByName(String name) {
        return chainRepository.existsByName(name);
    }
}