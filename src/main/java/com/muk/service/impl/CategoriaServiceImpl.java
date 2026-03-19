package com.muk.service.impl;

import com.muk.entities.Categoria;
import com.muk.repository.CategoriaRepository;
import com.muk.service.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;

    @Override
    public List<Categoria> findAll() {
        return repository.findAllByOrderByNombreAsc();
    }

    public Optional<Categoria> findById(Long id) {
        return repository.findById(id);
    }

    @Override
public void addIfMissing(String category) {

    if(repository.findByNombreIgnoreCase(category).isEmpty()) {

        Categoria nueva = new Categoria();
        nueva.setNombre(category);

        repository.save(nueva);
    }
}
}