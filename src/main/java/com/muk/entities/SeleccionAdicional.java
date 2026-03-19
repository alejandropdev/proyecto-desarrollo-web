package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seleccion_adicional")
@Getter
@Setter
@NoArgsConstructor
public class SeleccionAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_carrito_id", nullable = false)
    private ItemCarrito itemCarrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adicional_id", nullable = false)
    private Adicional adicional;

    @Column(nullable = false)
    private Double precio;

    public SeleccionAdicional(Long id, ItemCarrito itemCarrito, Adicional adicional, Double precio) {
        this.id = id;
        this.itemCarrito = itemCarrito;
        this.adicional = adicional;
        this.precio = precio;
    }
}
