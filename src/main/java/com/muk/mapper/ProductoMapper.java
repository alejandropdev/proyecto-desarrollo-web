package com.muk.mapper;

import com.muk.dto.ProductoResponseDto;
import com.muk.entities.Adicional;
import com.muk.entities.Producto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductoMapper {

    public ProductoResponseDto toDto(Producto producto) {
        if (producto == null) {
            return null;
        }
        ProductoResponseDto dto = new ProductoResponseDto();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setImagenUrl(producto.getImagenUrl());
        dto.setActivo(producto.getActivo());
        if (producto.getCategoria() != null) {
            dto.setCategoriaId(producto.getCategoria().getId());
        }
        if (producto.getAdicionalesPermitidos() != null) {
            dto.setAdicionalesPermitidosIds(producto.getAdicionalesPermitidos().stream()
                    .map(Adicional::getId)
                    .collect(Collectors.toList()));
        } else {
            dto.setAdicionalesPermitidosIds(Collections.emptyList());
        }
        return dto;
    }

    public List<ProductoResponseDto> toDtoList(List<Producto> productos) {
        if (productos == null) {
            return null;
        }
        return productos.stream().map(this::toDto).collect(Collectors.toList());
    }
}
