package com.muk.mapper;

import com.muk.dto.AdicionalResponseDto;
import com.muk.entities.Adicional;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AdicionalMapper {

    public AdicionalResponseDto toDto(Adicional adicional) {
        if (adicional == null) {
            return null;
        }
        AdicionalResponseDto dto = new AdicionalResponseDto();
        dto.setId(adicional.getId());
        dto.setNombre(adicional.getNombre());
        dto.setPrecio(adicional.getPrecio());
        dto.setActivo(adicional.getActivo());
        if (adicional.getCategoria() != null) {
            dto.setCategoriaId(adicional.getCategoria().getId());
        }
        return dto;
    }

    public List<AdicionalResponseDto> toDtoList(List<Adicional> adiciones) {
        if (adiciones == null) {
            return null;
        }
        return adiciones.stream().map(this::toDto).collect(Collectors.toList());
    }
}
