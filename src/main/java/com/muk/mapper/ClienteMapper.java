package com.muk.mapper;

import com.muk.dto.ClienteResponseDto;
import com.muk.entities.Cliente;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClienteMapper {

    public ClienteResponseDto toDto(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
        ClienteResponseDto dto = new ClienteResponseDto();
        dto.setId(cliente.getId());
        dto.setNombre(cliente.getNombre());
        dto.setApellido(cliente.getApellido());
        dto.setEmail(cliente.getEmail());
        dto.setTelefono(cliente.getTelefono());
        dto.setDireccion(cliente.getDireccion());
        return dto;
    }

    public List<ClienteResponseDto> toDtoList(List<Cliente> clientes) {
        if (clientes == null) {
            return null;
        }
        return clientes.stream().map(this::toDto).collect(Collectors.toList());
    }
}
