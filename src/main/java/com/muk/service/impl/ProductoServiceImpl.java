package com.muk.service.impl;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Adicional;
import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import com.muk.repository.AdicionalRepository;
import com.muk.repository.CategoriaRepository;
import com.muk.repository.ItemCarritoRepository;
import com.muk.repository.ProductoRepository;
import com.muk.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final AdicionalRepository adicionalRepository;

    @Autowired
    public ProductoServiceImpl(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository,
            ItemCarritoRepository itemCarritoRepository,
            AdicionalRepository adicionalRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.adicionalRepository = adicionalRepository;
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll().stream()
                .filter(this::isActivo)
                .toList();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(Long.valueOf(id))
                .filter(this::isActivo);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save((Producto) producto);
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
        producto.setAdicionalesPermitidos(resolveAdicionalesForCreate(categoria, request.adicionalesPermitidosIds()));
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
        List<Long> adIds = request.adicionalesPermitidosIds();
        if (adIds == null) {
            // Campo ausente en JSON: no tocar la lista actual
        } else if (adIds.isEmpty()) {
            producto.setAdicionalesPermitidos(adicionalRepository.findByCategoria_IdAndActivoTrueOrderByNombreAsc(
                    categoria.getId()));
        } else {
            producto.setAdicionalesPermitidos(resolveExplicitAdicionales(categoria, adIds));
        }
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

    private List<Adicional> resolveAdicionalesForCreate(Categoria categoria, List<Long> ids) {
        if (categoria.getId() == null) {
            throw new IllegalArgumentException("Categoría inválida.");
        }
        if (ids == null || ids.isEmpty()) {
            return adicionalRepository.findByCategoria_IdAndActivoTrueOrderByNombreAsc(categoria.getId());
        }
        return resolveExplicitAdicionales(categoria, ids);
    }

    private List<Adicional> resolveExplicitAdicionales(Categoria categoria, List<Long> ids) {
        if (categoria.getId() == null) {
            throw new IllegalArgumentException("Categoría inválida.");
        }
        List<Adicional> loaded = adicionalRepository.findAllById(ids);
        if (loaded.size() != ids.size()) {
            throw new IllegalArgumentException("Uno o más adicionales no existen.");
        }
        Set<Long> requested = new HashSet<>(ids);
        for (Adicional adicional : loaded) {
            if (!Boolean.TRUE.equals(adicional.getActivo())) {
                throw new IllegalArgumentException("Adicional inactivo: " + adicional.getId());
            }
            if (adicional.getCategoria() == null || adicional.getCategoria().getId() == null
                    || !adicional.getCategoria().getId().equals(categoria.getId())) {
                throw new IllegalArgumentException("El adicional no pertenece a la categoría del producto.");
            }
            requested.remove(adicional.getId());
        }
        if (!requested.isEmpty()) {
            throw new IllegalArgumentException("Uno o más adicionales no existen.");
        }
        return loaded;
    }

    @Override
    public List<com.muk.entities.Adicional> obtenerAdicionalesPermitidos(Long productoId) {
        if (productoId == null) {
            return new ArrayList<>();
        }
        Optional<Producto> producto = findById(productoId);
        if (producto.isEmpty()) {
            return new ArrayList<>();
        }
        List<com.muk.entities.Adicional> adicionalesPermitidos = producto.get().getAdicionalesPermitidos();
        return adicionalesPermitidos != null ? adicionalesPermitidos : new ArrayList<>();
    }
}