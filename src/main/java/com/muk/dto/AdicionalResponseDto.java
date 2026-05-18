package com.muk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdicionalResponseDto {
    private Long id;
    private String nombre;
    private Double precio;
    private Boolean activo;
    private Long categoriaId;
}
