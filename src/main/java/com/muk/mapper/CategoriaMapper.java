package com.muk.mapper;

import com.muk.dto.CategoriaResponseDto;
import com.muk.entities.Categoria;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CategoriaMapper {

    public CategoriaResponseDto toDto(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        CategoriaResponseDto dto = new CategoriaResponseDto();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        dto.setDescription(categoria.getDescription());
        return dto;
    }

    public List<CategoriaResponseDto> toDtoList(List<Categoria> categorias) {
        if (categorias == null) {
            return null;
        }
        return categorias.stream().map(this::toDto).collect(Collectors.toList());
    }
}
