package com.muk.mapper;

import com.muk.dto.OperadorResponseDto;
import com.muk.entities.Operador;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperadorMapper {

    OperadorResponseDto toDto(Operador operador);

    List<OperadorResponseDto> toDtoList(List<Operador> operadores);
}
