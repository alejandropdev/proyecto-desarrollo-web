package com.muk.service.impl;

import com.muk.entities.Producto;
import com.muk.repository.ItemCarritoRepository;
import com.muk.repository.ProductoRepository;
import com.muk.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final ItemCarritoRepository itemCarritoRepository;

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll().stream()
                .filter(this::isActivo)
                .toList();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id)
                .filter(this::isActivo);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public Producto saveFromAdminForm(Producto producto) {
        if (producto.getCategoria() != null && producto.getCategoria().getId() == null) {
            producto.setCategoria(null);
        }
        return save(producto);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // If a seeded product is currently referenced in cart items, remove those references first.
        itemCarritoRepository.deleteByProductoId(id);
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setActivo(false);
            productoRepository.save(producto);
        });
    }

    @Override
    public List<Producto> findByCategory(String categoria) {
        return productoRepository.findByCategoria_NombreIgnoreCase(categoria).stream()
                .filter(this::isActivo)
                .toList();
    }

    @Override
    public List<Producto> searchByName(String q) {
        return productoRepository.findByNombreContainingIgnoreCase(q).stream()
                .filter(this::isActivo)
                .toList();
    }

    @Override
    public List<Producto> findByFilters(String category, String query) {
        if (query != null && !query.isBlank()) {
            return searchByName(query.trim());
        }
        if (category != null && !category.isBlank()) {
            return findByCategory(category.trim());
        }
        return findAll();
    }

    private boolean isActivo(Producto producto) {
        return Boolean.TRUE.equals(producto.getActivo());
    }
}