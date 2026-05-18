package com.muk.mapper;

import com.muk.dto.PedidoResumenDto;
import com.muk.entities.Pedido;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PedidoMapper {

    @Mapping(target = "clienteId", source = "cliente.id")
    @Mapping(target = "fechaCreacion", expression = "java(pedido.getFechaCreacion() != null ? pedido.getFechaCreacion().toString() : null)")
    @Mapping(target = "fechaEntrega", expression = "java(pedido.getFechaEntrega() != null ? pedido.getFechaEntrega().toString() : null)")
    PedidoResumenDto toDto(Pedido pedido);

    List<PedidoResumenDto> toDtoList(List<Pedido> pedidos);
}
