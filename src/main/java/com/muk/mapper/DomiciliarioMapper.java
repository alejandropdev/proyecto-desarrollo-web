package com.muk.mapper;

import com.muk.dto.DomiciliarioResponseDto;
import com.muk.entities.Domiciliario;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class DomiciliarioMapper {

    public DomiciliarioResponseDto toDto(Domiciliario domiciliario) {
        if (domiciliario == null) {
            return null;
        }
        DomiciliarioResponseDto dto = new DomiciliarioResponseDto();
        dto.setId(domiciliario.getId());
        dto.setNombre(domiciliario.getNombre());
        dto.setCelular(domiciliario.getCelular());
        dto.setCedula(domiciliario.getCedula());
        dto.setDisponible(domiciliario.getDisponible());
        dto.setActivo(domiciliario.getActivo());
        return dto;
    }

    public List<DomiciliarioResponseDto> toDtoList(List<Domiciliario> domiciliarios) {
        if (domiciliarios == null) {
            return null;
        }
        return domiciliarios.stream().map(this::toDto).collect(Collectors.toList());
    }
}
