package com.muk.service.impl;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Adicional;
import com.muk.entities.Categoria;
import com.muk.repository.AdicionalRepository;
import com.muk.repository.CategoriaRepository;
import com.muk.service.AdicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AdicionalServiceImpl implements AdicionalService {

    private final AdicionalRepository repository;
    private final CategoriaRepository categoriaRepository;

    @Autowired
    public AdicionalServiceImpl(AdicionalRepository repository, CategoriaRepository categoriaRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    public List<Adicional> findAll() {
        return repository.findAll().stream()
                .filter(this::isActivo)
                .toList();
    }

    @Override
    public Optional<Adicional> findById(Long id) {
        return repository.findById(Long.valueOf(id)).filter(this::isActivo);
    }

    @Override
    public List<Adicional> findByCategoriaNombre(String nombre) {
        return repository.findByCategoria_NombreIgnoreCaseOrderByNombreAsc(nombre).stream()
                .filter(this::isActivo)
                .toList();
    }

    @Override
    public List<Adicional> findForMenuCategory(String category) {
        if (category == null || category.isBlank()) {
            return Collections.emptyList();
        }
        return findByCategoriaNombre(category.trim());
    }

    @Override
    public Adicional createAdicion(ApiDtos.AdicionalUpsertRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría inválida."));
        Adicional adicional = new Adicional();
        adicional.setNombre(request.nombre().trim());
        adicional.setPrecio(request.precio());
        adicional.setCategoria(categoria);
        adicional.setActivo(true);
        return repository.save(adicional);
    }

    @Override
    public Optional<Adicional> updateAdicion(Long id, ApiDtos.AdicionalUpsertRequest request) {
        Optional<Adicional> existing = findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría inválida."));
        Adicional adicional = existing.get();
        adicional.setNombre(request.nombre().trim());
        adicional.setPrecio(request.precio());
        adicional.setCategoria(categoria);
        return Optional.of(repository.save(adicional));
    }

    @Override
    public void delete(Long id) {
        repository.findById(Long.valueOf(id)).ifPresent(adicional -> {
            adicional.setActivo(false);
            repository.save(adicional);
        });
    }

    private boolean isActivo(Adicional adicional) {
        return Boolean.TRUE.equals(adicional.getActivo());
    }
}
