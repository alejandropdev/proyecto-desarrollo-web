package com.muk.service.impl;

import com.muk.entities.Producto;
import com.muk.repository.ProductoRepository;
import com.muk.service.ProductoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    public Producto save(Producto producto) {
        return productoRepository.save(producto);
    }

    @Override
    public void delete(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public List<Producto> findByCategory(String nombreCategoria) {
        return productoRepository.findByCategoria_NombreIgnoreCase(nombreCategoria);
    }

    @Override
    public List<Producto> searchByName(String q) {
        return productoRepository.findByNombreContainingIgnoreCase(q);
    }
}