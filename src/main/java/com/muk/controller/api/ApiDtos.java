package com.muk.controller.api;

import java.util.List;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record ProductoDto(Long id, String nombre, String descripcion, Double precio, String imagenUrl, boolean activo,
                              CategoriaDto categoria) {
    }

    public record CategoriaDto(Long id, String nombre, String description) {
    }

    public record AdicionalDto(Long id, String nombre, Double precio, boolean activo, CategoriaDto categoria) {
    }

    public record ClienteDto(Long id, String nombre, String apellido, String email, String telefono, String direccion) {
    }

    public record OperadorDto(Long id, String nombre, String usuario, boolean activo) {
    }

    public record MenuResponse(List<ProductoDto> productos, List<CategoriaDto> categorias, List<AdicionalDto> adiciones) {
    }

    public record ProductoUpsertRequest(String nombre, String descripcion, Double precio, String imagenUrl, Long categoriaId) {
    }

    public record CategoriaRequest(String nombre) {
    }

    public record AdicionalUpsertRequest(String nombre, Double precio, Long categoriaId) {
    }

    public record OperadorRequest(String nombre, String usuario, String contrasena) {
    }

    public record ClienteUpsertRequest(String nombre, String apellido, String email, String telefono, String direccion,
                                       String contrasena) {
    }

    public record LoginRequest(String email, String password) {
    }

    public record AdminLoginRequest(String usuario, String password) {
    }

    public record MessageResponse(String message) {
    }

    public record SeleccionAdicionalDto(Long id, AdicionalDto adicional, Double precio) {
    }

    public record ItemCarritoDto(Long id, ProductoDto producto, Integer cantidad, Double precioUnitario,
                                  List<SeleccionAdicionalDto> selecciones) {
    }

    public record DomiciliarioDto(Long id, String nombre, String celular, String cedula, Boolean disponible) {
    }

    public record PedidoDto(Long id, ClienteDto cliente, OperadorDto operador, DomiciliarioDto domiciliario,
                            String estado, String fechaCreacion, String fechaEntrega, List<ItemCarritoDto> items) {
    }
}
