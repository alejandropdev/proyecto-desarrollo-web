package com.muk.service;

import com.muk.controller.api.ApiDtos;
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

    Producto createProducto(ApiDtos.ProductoUpsertRequest request);

    Optional<Producto> updateProducto(Long id, ApiDtos.ProductoUpsertRequest request);

    List<Producto> findByFilters(String category, String query);

    void delete(Long id);

    List<Producto> findByCategory(String category);

    List<Producto> searchByName(String query);
}
