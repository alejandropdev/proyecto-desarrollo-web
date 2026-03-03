package com.muk.service;

import java.util.List;

/**
 * Interfaz del servicio para categorías de productos.
 */
public interface CategoriaService {

    List<String> findAll();

    void addIfMissing(String category);
}
