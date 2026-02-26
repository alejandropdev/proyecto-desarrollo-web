package com.muk.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad Producto. ID Long permite null en creación (alta).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Producto {

    private Long id;
    private String name;
    private String description;
    private Long price;
    private String imageUrl;
    private String category;
    private Boolean available;
}
