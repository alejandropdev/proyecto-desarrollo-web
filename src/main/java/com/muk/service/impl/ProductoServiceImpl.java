package com.muk.service.impl;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import com.muk.repository.CategoriaRepository;
import com.muk.repository.ItemCarritoRepository;
import com.muk.repository.ProductoRepository;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ItemCarritoRepository itemCarritoRepository;

    @Autowired
    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            ItemCarritoRepository itemCarritoRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.itemCarritoRepository = itemCarritoRepository;
    }

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
    public Producto createProducto(ApiDtos.ProductoUpsertRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría inválida."));
        Producto producto = new Producto();
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion().trim());
        producto.setPrecio(request.precio());
        producto.setImagenUrl(request.imagenUrl().trim());
        producto.setCategoria(categoria);
        producto.setActivo(true);
        return save(producto);
    }

    @Override
    public Optional<Producto> updateProducto(Long id, ApiDtos.ProductoUpsertRequest request) {
        Optional<Producto> existing = findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría inválida."));
        Producto producto = existing.get();
        producto.setNombre(request.nombre().trim());
        producto.setDescripcion(request.descripcion().trim());
        producto.setPrecio(request.precio());
        producto.setImagenUrl(request.imagenUrl().trim());
        producto.setCategoria(categoria);
        return Optional.of(save(producto));
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

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        // Seeded cart items reference first products; remove dependent rows before delete/disable.
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

    private boolean isActivo(Producto producto) {
        return Boolean.TRUE.equals(producto.getActivo());
    }
}