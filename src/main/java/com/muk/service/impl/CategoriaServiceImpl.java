package com.muk.service.impl;

import com.muk.entities.Categoria;
import com.muk.repository.CategoriaRepository;
import com.muk.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;

    @Autowired
    public CategoriaServiceImpl(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Categoria> findAll() {
        return repository.findAllByOrderByNombreAsc();
    }

    public Optional<Categoria> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        return repository.findByNombreIgnoreCase(nombre);
    }

    @Override
    public Categoria createOrGetByNombre(String nombre) {
        String normalized = nombre == null ? "" : nombre.trim();
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("El nombre de categoría es obligatorio.");
        }
        return repository.findByNombreIgnoreCase(normalized).orElseGet(() -> {
            Categoria nueva = new Categoria();
            nueva.setNombre(normalized);
            return repository.save(nueva);
        });
    }

    @Override
    public void addIfMissing(String category) {
        if (category == null || category.isBlank()) {
            return;
        }
        createOrGetByNombre(category);
    }
}