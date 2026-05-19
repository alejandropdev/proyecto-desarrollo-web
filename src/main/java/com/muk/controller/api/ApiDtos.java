package com.muk.controller.api;

import java.util.List;

public final class ApiDtos {
    private ApiDtos() {
    }

    // ===================== PRODUCTOS =====================
    public record ProductoDto(
            Long id,
            String nombre,
            String descripcion,
            Double precio,
            String imagenUrl,
            boolean activo,
            Long categoriaId,
            List<Long> adicionalesPermitidosIds
    ) {}

    public record CategoriaDto(Long id, String nombre, String description) {}

    public record AdicionalDto(
            Long id,
            String nombre,
            Double precio,
            boolean activo,
            Long categoriaId
    ) {}

    // ===================== CLIENTES =====================
    public record ClienteDto(
            Long id,
            String nombre,
            String apellido,
            String email,
            String telefono,
            String direccion
    ) {}

    // ===================== OPERADORES =====================
    public record OperadorDto(Long id, String nombre, String usuario, boolean activo) {}

    public record MenuProductoDto(
            Long id,
            String nombre,
            String descripcion,
            Double precio,
            String imagenUrl,
            boolean activo,
            Long categoriaId
    ) {}

    public record MenuResponse(
            List<MenuProductoDto> productos,
            List<CategoriaDto> categorias,
            List<AdicionalDto> adiciones
    ) {}

    // ===================== REQUESTS =====================
    public record ProductoUpsertRequest(
            String nombre,
            String descripcion,
            Double precio,
            String imagenUrl,
            Long categoriaId,
            List<Long> adicionalesPermitidosIds
    ) {
        public ProductoUpsertRequest {
            adicionalesPermitidosIds =
                    adicionalesPermitidosIds == null ? null : List.copyOf(adicionalesPermitidosIds);
        }
    }

    public record CategoriaRequest(String nombre) {}

    public record AdicionalUpsertRequest(
            String nombre,
            Double precio,
            Long categoriaId
    ) {}

    public record OperadorRequest(
            String nombre,
            String usuario,
            String contrasena
    ) {}

    public record ClienteUpsertRequest(
            String nombre,
            String apellido,
            String email,
            String telefono,
            String direccion,
            String contrasena
    ) {}

    public record LoginRequest(String email, String password) {}

    public record AdminLoginRequest(String usuario, String password) {}

    public record OperarioLoginRequest(String usuario, String password) {}

    public record OperarioLoginResponse(
            String message,
            Long id,
            String usuario,
            String nombre
    ) {}

    public record MessageResponse(String message) {}

    // ===================== PEDIDOS =====================
    public record CrearPedidoRequest(List<ItemPedidoRequest> items) {}

    public record ItemPedidoRequest(
            Long productoId,
            Integer cantidad,
            List<SeleccionAdicionalRequest> adiciones
    ) {}

    public record SeleccionAdicionalRequest(Long adicionalId, Double precio) {}

    // 🔹 LISTADO NORMAL (clientes)
    public record PedidoDto(
            Long id,
            Long clienteId,
            Integer cantidadProductos,
            Integer cantidadAdiciones,
            String estado,
            String fechaCreacion,
            String fechaEntrega
    ) {}

    // 🔹 LISTADO OPERARIO (CON DOMICILIARIO)
    public record PedidoOperadorDto(
            Long id,
            Long clienteId,
            Integer cantidadProductos,
            Integer cantidadAdiciones,
            String estado,
            String fechaCreacion,
            String fechaEntrega,
            Long domiciliarioId,
            String domiciliarioNombre,
            Boolean domiciliarioDisponible
    ) {}

    // 🔹 DETALLE COMPLETO
    public record PedidoDetalleDto(
            Long id,
            ClienteDto cliente,
            Integer cantidadProductos,
            Integer cantidadAdiciones,
            String estado,
            String fechaCreacion,
            String fechaEntrega,
            List<ItemPedidoDto> items
    ) {}

    public record ItemPedidoDto(
            Long id,
            Long productoId,
            String productoNombre,
            Double productoPrecio,
            Integer cantidad,
            Double precioUnitario,
            List<SeleccionAdicionalPedidoDto> selecciones
    ) {}

    public record SeleccionAdicionalPedidoDto(
            Long id,
            Long adicionalId,
            String adicionalNombre,
            Double precio
    ) {}

    // ===================== DOMICILIARIOS =====================
   public record DomiciliarioDto(
        Long id,
        String nombre,
        String celular,
        String cedula,
        Boolean disponible,
        Boolean activo
) {}
    public record DomiciliarioUpsertRequest(
            String nombre,
            String celular,
            String cedula
    ) {}

    // 🔥 CORREGIDO (nombre consistente con controller)
    public record CambiarEstadoPedidoRequest(String nuevoEstado) {}

    // 🔥 NECESARIO para asignar domiciliario
    public record AsignarDomiciliarioRequest(Long domiciliarioId) {}
}