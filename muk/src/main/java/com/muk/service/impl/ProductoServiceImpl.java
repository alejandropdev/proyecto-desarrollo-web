package com.muk.service.impl;

import com.muk.entities.Producto;
import com.muk.repository.ProductoRepository;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio. Única capa que llama al repositorio.
 */
@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository repository;

    @Override
    public List<Producto> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    @Override
    public Producto save(Producto producto) {
        if (producto == null) return null;
        return repository.save(producto);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public List<Producto> findByCategory(String category) {
        if (category == null || category.isBlank()) return List.of();
        return repository.findByCategory(category.trim());
    }

    @Override
    public List<Producto> searchByName(String query) {
        if (query == null || query.isBlank()) return List.of();
        return repository.searchByName(query.trim());
    }
}
