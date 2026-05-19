package com.muk.controller.api;

import com.muk.entities.*;

import java.util.List;

public final class ApiMappers {
    private ApiMappers() {}

    // ===================== PRODUCTOS =====================
    public static ApiDtos.MenuProductoDto toMenuProductoDto(Producto producto) {
        return new ApiDtos.MenuProductoDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getImagenUrl(),
                Boolean.TRUE.equals(producto.getActivo()),
                producto.getCategoria() == null ? null : producto.getCategoria().getId()
        );
    }

    public static ApiDtos.ProductoDto toProductoDto(Producto producto) {
        Long categoriaId = producto.getCategoria() == null ? null : producto.getCategoria().getId();

        List<Long> adicionalesIds = producto.getAdicionalesPermitidos() == null
                ? List.of()
                : producto.getAdicionalesPermitidos().stream().map(Adicional::getId).toList();

        return new ApiDtos.ProductoDto(
                producto.getId(),
                producto.getNombre(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getImagenUrl(),
                Boolean.TRUE.equals(producto.getActivo()),
                categoriaId,
                adicionalesIds
        );
    }

    public static ApiDtos.CategoriaDto toCategoriaDto(Categoria categoria) {
        return new ApiDtos.CategoriaDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getDescription()
        );
    }

    public static ApiDtos.AdicionalDto toAdicionalDto(Adicional adicional) {
        Long categoriaId = adicional.getCategoria() == null ? null : adicional.getCategoria().getId();

        return new ApiDtos.AdicionalDto(
                adicional.getId(),
                adicional.getNombre(),
                adicional.getPrecio(),
                Boolean.TRUE.equals(adicional.getActivo()),
                categoriaId
        );
    }

    // ===================== CLIENTES =====================
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

    // ===================== OPERADORES =====================
    public static ApiDtos.OperadorDto toOperadorDto(Operador operador) {
        return new ApiDtos.OperadorDto(
                operador.getId(),
                operador.getNombre(),
                operador.getUsuario(),
                Boolean.TRUE.equals(operador.getActivo())
        );
    }

    // ===================== PEDIDOS =====================

    // 🔹 LISTADO SIMPLE (cliente)
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

    // 🔹 LISTADO OPERARIO (CON DOMICILIARIO) 🔥
    public static ApiDtos.PedidoOperadorDto toPedidoOperadorDto(Pedido pedido) {
        return new ApiDtos.PedidoOperadorDto(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCantidadProductos(),
                pedido.getCantidadAdiciones(),
                pedido.getEstado(),
                pedido.getFechaCreacion() != null ? pedido.getFechaCreacion().toString() : null,
                pedido.getFechaEntrega() != null ? pedido.getFechaEntrega().toString() : null,
                pedido.getDomiciliario() != null ? pedido.getDomiciliario().getId() : null,
                pedido.getDomiciliario() != null ? pedido.getDomiciliario().getNombre() : null,
                pedido.getDomiciliario() != null ? pedido.getDomiciliario().getDisponible() : null
        );
    }

    // 🔹 DETALLE COMPLETO
    public static ApiDtos.PedidoDetalleDto toPedidoDetalleDto(Pedido pedido) {
        return new ApiDtos.PedidoDetalleDto(
                pedido.getId(),
                toClienteDto(pedido.getCliente()),
                pedido.getCantidadProductos(),
                pedido.getCantidadAdiciones(),
                pedido.getEstado(),
                pedido.getFechaCreacion() != null ? pedido.getFechaCreacion().toString() : null,
                pedido.getFechaEntrega() != null ? pedido.getFechaEntrega().toString() : null,
                pedido.getItems() != null
                        ? pedido.getItems().stream().map(ApiMappers::toItemPedidoDto).toList()
                        : List.of()
        );
    }

    public static ApiDtos.ItemPedidoDto toItemPedidoDto(ItemPedido itemPedido) {
        Producto p = itemPedido.getProducto();
        return new ApiDtos.ItemPedidoDto(
                itemPedido.getId(),
                p == null ? null : p.getId(),
                p == null ? null : p.getNombre(),
                p == null ? null : p.getPrecio(),
                itemPedido.getCantidad(),
                itemPedido.getPrecioUnitario(),
                itemPedido.getSelecciones() != null
                        ? itemPedido.getSelecciones().stream()
                        .map(ApiMappers::toSeleccionAdicionalPedidoDto)
                        .toList()
                        : List.of()
        );
    }

    public static ApiDtos.SeleccionAdicionalPedidoDto toSeleccionAdicionalPedidoDto(
            SeleccionAdicionalPedido seleccion
    ) {
        Adicional a = seleccion.getAdicional();
        return new ApiDtos.SeleccionAdicionalPedidoDto(
                seleccion.getId(),
                a == null ? null : a.getId(),
                a == null ? null : a.getNombre(),
                seleccion.getPrecio()
        );
    }

    // ===================== DOMICILIARIOS =====================
  public static ApiDtos.DomiciliarioDto toDomiciliarioDto(Domiciliario domiciliario) {
    return new ApiDtos.DomiciliarioDto(
            domiciliario.getId(),
            domiciliario.getNombre(),
            domiciliario.getCelular(),
            domiciliario.getCedula(),
            domiciliario.getDisponible(),
            domiciliario.getActivo()
    );
}
}