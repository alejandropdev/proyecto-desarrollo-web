package com.muk.service.impl;

import com.muk.entities.Producto;
import com.muk.repository.ProductoRepository;
import com.muk.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

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
    public void delete(Long id) {
        if (id != null) {
            productoRepository.deleteById(id);
        }
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