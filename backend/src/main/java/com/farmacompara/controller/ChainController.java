package com.farmacompara.controller;

import com.farmacompara.model.Chain;
import com.farmacompara.service.ChainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador de cadenas de farmacias
 */
@RestController
@RequestMapping("/api/chains")
public class ChainController {

    @Autowired
    private ChainService chainService;

    @GetMapping
    public ResponseEntity<List<Chain>> findAll() {
        return ResponseEntity.ok(chainService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chain> findById(@PathVariable UUID id) {
        return chainService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}