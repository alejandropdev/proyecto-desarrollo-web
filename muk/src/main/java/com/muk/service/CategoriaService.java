package com.muk.service;

import java.util.List;

/**
 * Servicio para administrar categorías.
 */
public interface CategoriaService {

    List<String> findAll();

    void addIfMissing(String category);
}
