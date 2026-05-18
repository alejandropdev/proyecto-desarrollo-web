package com.muk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResumenDto {
    private Long id;
    private Long clienteId;
    private Integer cantidadProductos;
    private Integer cantidadAdiciones;
    private String estado;
    private String fechaCreacion;
    private String fechaEntrega;
}
