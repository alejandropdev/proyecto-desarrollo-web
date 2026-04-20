package com.muk.controller.api;

import com.muk.entities.Adicional;
import com.muk.entities.Categoria;
import com.muk.entities.Cliente;
import com.muk.entities.Domiciliario;
import com.muk.entities.ItemCarrito;
import com.muk.entities.Operador;
import com.muk.entities.Pedido;
import com.muk.entities.Producto;
import com.muk.entities.SeleccionAdicional;

import java.util.List;
import java.util.stream.Collectors;

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

    public static ApiDtos.DomiciliarioDto toDomiciliarioDto(Domiciliario domiciliario) {
        if (domiciliario == null) return null;
        return new ApiDtos.DomiciliarioDto(
                domiciliario.getId(),
                domiciliario.getNombre(),
                domiciliario.getCelular(),
                domiciliario.getCedula(),
                domiciliario.getDisponible()
        );
    }

    public static ApiDtos.SeleccionAdicionalDto toSeleccionAdicionalDto(SeleccionAdicional seleccion) {
        if (seleccion == null) return null;
        ApiDtos.AdicionalDto adicionalDto = seleccion.getAdicional() == null ? null : toAdicionalDto(seleccion.getAdicional());
        return new ApiDtos.SeleccionAdicionalDto(
                seleccion.getId(),
                adicionalDto,
                seleccion.getPrecio()
        );
    }

    public static ApiDtos.ItemCarritoDto toItemCarritoDto(ItemCarrito itemCarrito) {
        if (itemCarrito == null) return null;
        ApiDtos.ProductoDto productoDto = itemCarrito.getProducto() == null ? null : toProductoDto(itemCarrito.getProducto());
        List<ApiDtos.SeleccionAdicionalDto> selecciones = itemCarrito.getSelecciones() == null ? 
                List.of() : itemCarrito.getSelecciones().stream()
                .map(ApiMappers::toSeleccionAdicionalDto)
                .collect(Collectors.toList());
        return new ApiDtos.ItemCarritoDto(
                itemCarrito.getId(),
                productoDto,
                itemCarrito.getCantidad(),
                itemCarrito.getPrecioUnitario(),
                selecciones
        );
    }

    public static ApiDtos.PedidoDto toPedidoDto(Pedido pedido) {
        if (pedido == null) return null;
        ApiDtos.ClienteDto clienteDto = pedido.getCliente() == null ? null : toClienteDto(pedido.getCliente());
        ApiDtos.OperadorDto operadorDto = pedido.getOperador() == null ? null : toOperadorDto(pedido.getOperador());
        ApiDtos.DomiciliarioDto domiciliarioDto = pedido.getDomiciliario() == null ? null : toDomiciliarioDto(pedido.getDomiciliario());
        return new ApiDtos.PedidoDto(
                pedido.getId(),
                clienteDto,
                operadorDto,
                domiciliarioDto,
                pedido.getEstado(),
                pedido.getFechaCreacion() == null ? null : pedido.getFechaCreacion().toString(),
                pedido.getFechaEntrega() == null ? null : pedido.getFechaEntrega().toString(),
                List.of()  // Para esta vista de lectura, dejamos vacío. Se poblaría si necesitamos en el detalle
        );
    }
}
