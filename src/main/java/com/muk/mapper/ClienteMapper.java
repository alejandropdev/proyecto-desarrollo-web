package com.muk.mapper;

import com.muk.dto.ClienteResponseDto;
import com.muk.entities.Cliente;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ClienteMapper {

    ClienteResponseDto toDto(Cliente cliente);

    List<ClienteResponseDto> toDtoList(List<Cliente> clientes);
}
