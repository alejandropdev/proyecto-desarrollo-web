package com.muk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DomiciliarioResponseDto {
    private Long id;
    private String nombre;
    private String celular;
    private String cedula;
    private Boolean disponible;
    private Boolean activo;
}
