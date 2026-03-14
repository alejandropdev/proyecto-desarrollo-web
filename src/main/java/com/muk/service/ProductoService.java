package com.muk.service;

import com.muk.entities.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de productos. Lógica de negocio.
 */
public interface ProductoService {

    List<Producto> findAll();

    Optional<Producto> findById(Long id);

    Producto save(Producto producto);

    void delete(Long id);

    List<Producto> findByCategory(String category);

    List<Producto> searchByName(String query);
}
