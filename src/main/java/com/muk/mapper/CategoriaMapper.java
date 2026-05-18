package com.muk.mapper;

import com.muk.dto.CategoriaResponseDto;
import com.muk.entities.Categoria;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    CategoriaResponseDto toDto(Categoria categoria);

    List<CategoriaResponseDto> toDtoList(List<Categoria> categorias);
}
