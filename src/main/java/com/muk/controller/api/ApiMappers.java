package com.muk.controller.api;

import com.muk.entities.Adicional;
import com.muk.entities.Categoria;
import com.muk.entities.Cliente;
import com.muk.entities.Operador;
import com.muk.entities.Producto;

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
        return new ApiDtos.AdicionalDto(adicional.getId(), adicional.getNombre(), adicional.getPrecio(), Boolean.TRUE.equals(adicional.getActivo()));
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
}
