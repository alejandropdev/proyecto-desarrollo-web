package com.muk.service;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Adicional;

import java.util.List;
import java.util.Optional;

public interface AdicionalService {

    List<Adicional> findAll();

    Optional<Adicional> findById(Long id);

    List<Adicional> findByCategoriaNombre(String nombre);

    List<Adicional> findForMenuCategory(String category);

    List<Adicional> findActivosByCategoriaId(Long categoriaId);

    Adicional createAdicion(ApiDtos.AdicionalUpsertRequest request);

    Optional<Adicional> updateAdicion(Long id, ApiDtos.AdicionalUpsertRequest request);

    void delete(Long id);
}
