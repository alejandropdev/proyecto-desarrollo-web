package com.muk.service.impl;

import com.muk.entities.Categoria;
import com.muk.repository.CategoriaRepository;
import com.muk.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    @Autowired
    private CategoriaRepository repository;

  @Override
public List<Categoria> findAll() {
    return repository.findAll();
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