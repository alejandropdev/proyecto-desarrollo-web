package com.muk.service;

import com.muk.entities.Categoria;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para administrar categorías.
 */
public interface CategoriaService {

    List<Categoria> findAll();

    Optional<Categoria> findById(Long id);

    Optional<Categoria> findByNombre(String nombre);

    void addIfMissing(String nombre);
}
