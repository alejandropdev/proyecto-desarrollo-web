package com.muk.service.impl;

import com.muk.entities.Adicional;
import com.muk.repository.AdicionalRepository;
import com.muk.service.AdicionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdicionalServiceImpl implements AdicionalService {

    private final AdicionalRepository repository;

    @Override
    public List<Adicional> findByCategoriaNombre(String nombre) {
        return repository.findByCategoria_NombreIgnoreCaseOrderByNombreAsc(nombre);
    }
}
