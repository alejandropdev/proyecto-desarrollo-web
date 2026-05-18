package com.muk.mapper;

import com.muk.dto.OperadorResponseDto;
import com.muk.entities.Operador;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OperadorMapper {

    public OperadorResponseDto toDto(Operador operador) {
        if (operador == null) {
            return null;
        }
        OperadorResponseDto dto = new OperadorResponseDto();
        dto.setId(operador.getId());
        dto.setNombre(operador.getNombre());
        dto.setUsuario(operador.getUsuario());
        dto.setActivo(operador.getActivo());
        return dto;
    }

    public List<OperadorResponseDto> toDtoList(List<Operador> operadores) {
        if (operadores == null) {
            return null;
        }
        return operadores.stream().map(this::toDto).collect(Collectors.toList());
    }
}
