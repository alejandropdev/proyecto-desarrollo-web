package com.muk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperadorResponseDto {
    private Long id;
    private String nombre;
    private String usuario;
    private Boolean activo;
}
