package com.muk.mapper;

import com.muk.dto.DomiciliarioResponseDto;
import com.muk.entities.Domiciliario;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DomiciliarioMapper {

    DomiciliarioResponseDto toDto(Domiciliario domiciliario);

    List<DomiciliarioResponseDto> toDtoList(List<Domiciliario> domiciliarios);
}
