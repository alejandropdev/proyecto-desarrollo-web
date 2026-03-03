package com.muk.service.impl;

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
    public List<String> findAll() {
        return repository.findAll();
    }

    @Override
    public void addIfMissing(String category) {
        repository.add(category);
    }
}
