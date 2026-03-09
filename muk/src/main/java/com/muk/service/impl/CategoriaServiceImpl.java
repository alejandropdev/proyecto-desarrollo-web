package com.muk.service.impl;

import com.muk.entities.Categoria;
import com.muk.repository.CategoriaRepository;
import com.muk.service.CategoriaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaServiceImpl(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Categoria> findAll() {
        return repository.findAllByOrderByNombreAsc();
    }

    @Override
    public Optional<Categoria> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Categoria> findByNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return Optional.empty();
        }
        return repository.findByNombreIgnoreCase(nombre.trim());
    }

    @Override
    public void addIfMissing(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return;
        }
        String normalized = nombre.trim();
        if (repository.findByNombreIgnoreCase(normalized).isEmpty()) {
            Categoria c = new Categoria();
            c.setNombre(normalized);
            repository.save(c);
        }
    }
}
