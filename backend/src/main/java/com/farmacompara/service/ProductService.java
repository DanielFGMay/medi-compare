package com.farmacompara.service;

import com.farmacompara.model.Product;
import com.farmacompara.model.Category;
import com.farmacompara.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> findByIsRxTrue() {
        return productRepository.findByIsRxTrue();
    }

    public List<Product> searchByName(String name) {
        return productRepository.searchByName(name);
    }

    public List<Product> findByCategoryId(UUID categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    public Optional<Product> findById(UUID id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void deleteById(UUID id) {
        productRepository.deleteById(id);
    }
}