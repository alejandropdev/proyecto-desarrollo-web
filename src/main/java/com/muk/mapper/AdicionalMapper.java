package com.muk.mapper;

import com.muk.dto.AdicionalResponseDto;
import com.muk.entities.Adicional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AdicionalMapper {

    @Mapping(target = "categoriaId", source = "categoria.id")
    AdicionalResponseDto toDto(Adicional adicional);

    List<AdicionalResponseDto> toDtoList(List<Adicional> adiciones);
}
