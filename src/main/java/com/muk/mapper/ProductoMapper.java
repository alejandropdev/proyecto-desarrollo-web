package com.muk.mapper;

import com.muk.dto.ProductoResponseDto;
import com.muk.entities.Adicional;
import com.muk.entities.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "categoriaId", source = "categoria.id")
    @Mapping(target = "adicionalesPermitidosIds", source = "adicionalesPermitidos")
    ProductoResponseDto toDto(Producto producto);

    List<ProductoResponseDto> toDtoList(List<Producto> productos);

    // Usado por MapStruct para mapear cada elemento Adicional → Long
    default Long map(Adicional adicional) {
        return adicional == null ? null : adicional.getId();
    }
}
