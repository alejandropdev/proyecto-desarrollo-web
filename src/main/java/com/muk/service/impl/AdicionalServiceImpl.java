package com.muk.service.impl;

import com.muk.entities.Adicional;
import com.muk.repository.AdicionalRepository;
import com.muk.service.AdicionalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdicionalServiceImpl implements AdicionalService {

    private final AdicionalRepository repository;

    public AdicionalServiceImpl(AdicionalRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Adicional> findByCategoriaNombre(String nombre) {
        return repository.findByCategoria_NombreIgnoreCaseOrderByNombreAsc(nombre);
    }
}
