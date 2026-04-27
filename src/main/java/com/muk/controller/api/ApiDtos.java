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

    public record OperarioLoginRequest(String usuario, String password) {
    }

    public record OperarioLoginResponse(String message, Long id, String usuario, String nombre) {
    }

    public record MessageResponse(String message) {
    }

    // Pedidos Requests
    public record CrearPedidoRequest(List<ItemPedidoRequest> items) {
    }

    public record ItemPedidoRequest(Long productoId, Integer cantidad, List<SeleccionAdicionalRequest> adiciones) {
    }

    public record SeleccionAdicionalRequest(Long adicionalId, Double precio) {
    }

    // Pedidos Response DTOs - Listado (simplificado)
    public record PedidoDto(Long id, Long clienteId, Integer cantidadProductos, Integer cantidadAdiciones,
                            String estado, String fechaCreacion, String fechaEntrega) {
    }

    // Pedidos Response DTOs - Detalles (completo con items)
    public record ItemPedidoDto(Long id, ProductoDto producto, Integer cantidad, Double precioUnitario,
                                List<SeleccionAdicionalPedidoDto> selecciones) {
    }

    public record SeleccionAdicionalPedidoDto(Long id, AdicionalDto adicional, Double precio) {
    }

    public record PedidoDetalleDto(Long id, ClienteDto cliente, Integer cantidadProductos, Integer cantidadAdiciones,
                                   String estado, String fechaCreacion, String fechaEntrega,
                                   List<ItemPedidoDto> items) {
    }

    // Domiciliarios DTOs
    /**
     * DTO para listar domiciliarios (información resumida)
     */
    public record DomiciliarioDto(Long id, String nombre, String celular, String cedula, Boolean activo, Boolean disponible) {
    }

    /**
     * Request para crear/actualizar un domiciliario
     */
    public record DomiciliarioUpsertRequest(String nombre, String celular, String cedula) {
    }

    /**
     * Request para cambiar el estado de un pedido
     */
    public record CambiarEstadoPedidoRequest(String nuevoEstado) {
    }

    /**
     * Request para asignar un domiciliario a un pedido
     */
    public record AsignarDomiciliarioRequest(Long domiciliarioId) {
    }

    /**
     * Response para cambiar estado de pedido
     */
    public record CambiarEstadoResponse(Long pedidoId, String estadoAnterior, String nuevoEstado, Boolean exito, String mensaje) {
    }

    /**
     * DTO para pedido con domiciliario (para operador)
     */
    public record PedidoOperadorDto(Long id, Long clienteId, Integer cantidadProductos, Integer cantidadAdiciones,
                                    String estado, String fechaCreacion, String fechaEntrega,
                                    Long domiciliarioId, String domiciliarioNombre, Boolean domiciliarioDisponible) {
    }
}
