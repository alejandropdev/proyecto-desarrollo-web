package com.muk.mapper;

import com.muk.dto.PedidoResumenDto;
import com.muk.entities.Pedido;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PedidoMapper {

    public PedidoResumenDto toDto(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        PedidoResumenDto dto = new PedidoResumenDto();
        dto.setId(pedido.getId());
        if (pedido.getCliente() != null) {
            dto.setClienteId(pedido.getCliente().getId());
        }
        dto.setCantidadProductos(pedido.getCantidadProductos());
        dto.setCantidadAdiciones(pedido.getCantidadAdiciones());
        dto.setEstado(pedido.getEstado());
        dto.setFechaCreacion(pedido.getFechaCreacion() != null ? pedido.getFechaCreacion().toString() : null);
        dto.setFechaEntrega(pedido.getFechaEntrega() != null ? pedido.getFechaEntrega().toString() : null);
        return dto;
    }

    public List<PedidoResumenDto> toDtoList(List<Pedido> pedidos) {
        if (pedidos == null) {
            return null;
        }
        return pedidos.stream().map(this::toDto).collect(Collectors.toList());
    }
}
