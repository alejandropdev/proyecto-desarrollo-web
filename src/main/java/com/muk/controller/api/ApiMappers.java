package com.muk.controller.api;

import com.muk.entities.*;

import java.util.List;

public final class ApiMappers {
    private ApiMappers() {
    }

    public static ApiDtos.ProductoDto toProductoDto(Producto producto) {
        ApiDtos.CategoriaDto categoria = producto.getCategoria() == null ? null : toCategoriaDto(producto.getCategoria());
        return new ApiDtos.ProductoDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getImagenUrl(),
                Boolean.TRUE.equals(producto.getActivo()),
                categoria
        );
    }

    public static ApiDtos.CategoriaDto toCategoriaDto(Categoria categoria) {
        return new ApiDtos.CategoriaDto(categoria.getId(), categoria.getNombre(), categoria.getDescription());
    }

    public static ApiDtos.AdicionalDto toAdicionalDto(Adicional adicional) {
        ApiDtos.CategoriaDto categoria = adicional.getCategoria() == null ? null : toCategoriaDto(adicional.getCategoria());
        return new ApiDtos.AdicionalDto(
                adicional.getId(),
                adicional.getNombre(),
                adicional.getPrecio(),
                Boolean.TRUE.equals(adicional.getActivo()),
                categoria
        );
    }

    public static ApiDtos.ClienteDto toClienteDto(Cliente cliente) {
        return new ApiDtos.ClienteDto(
                cliente.getId(),
                cliente.getNombre(),
                cliente.getApellido(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.getDireccion()
        );
    }

    public static ApiDtos.OperadorDto toOperadorDto(Operador operador) {
        return new ApiDtos.OperadorDto(operador.getId(), operador.getNombre(), operador.getUsuario(), Boolean.TRUE.equals(operador.getActivo()));
    }

    // Mappers para Pedidos
    public static ApiDtos.PedidoDto toPedidoDto(Pedido pedido) {
        return new ApiDtos.PedidoDto(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCantidadProductos(),
                pedido.getCantidadAdiciones(),
                pedido.getEstado(),
                pedido.getFechaCreacion() != null ? pedido.getFechaCreacion().toString() : null,
                pedido.getFechaEntrega() != null ? pedido.getFechaEntrega().toString() : null
        );
    }

    public static ApiDtos.PedidoDetalleDto toPedidoDetalleDto(Pedido pedido) {
        return new ApiDtos.PedidoDetalleDto(
                pedido.getId(),
                toClienteDto(pedido.getCliente()),
                pedido.getCantidadProductos(),
                pedido.getCantidadAdiciones(),
                pedido.getEstado(),
                pedido.getFechaCreacion() != null ? pedido.getFechaCreacion().toString() : null,
                pedido.getFechaEntrega() != null ? pedido.getFechaEntrega().toString() : null,
                pedido.getItems() != null ? pedido.getItems().stream().map(ApiMappers::toItemPedidoDto).toList() : List.of()
        );
    }

    public static ApiDtos.ItemPedidoDto toItemPedidoDto(ItemPedido itemPedido) {
        return new ApiDtos.ItemPedidoDto(
                itemPedido.getId(),
                toProductoDto(itemPedido.getProducto()),
                itemPedido.getCantidad(),
                itemPedido.getPrecioUnitario(),
                itemPedido.getSelecciones() != null ? itemPedido.getSelecciones().stream().map(ApiMappers::toSeleccionAdicionalPedidoDto).toList() : List.of()
        );
    }

    public static ApiDtos.SeleccionAdicionalPedidoDto toSeleccionAdicionalPedidoDto(SeleccionAdicionalPedido seleccion) {
        return new ApiDtos.SeleccionAdicionalPedidoDto(
                seleccion.getId(),
                toAdicionalDto(seleccion.getAdicional()),
                seleccion.getPrecio()
        );
    }
}
