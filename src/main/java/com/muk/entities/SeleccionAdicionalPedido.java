package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seleccion_adicional_pedido")
@Getter
@Setter
@NoArgsConstructor
public class SeleccionAdicionalPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adicional_id", nullable = false)
    private Adicional adicional;

    @Column(nullable = false)
    private Double precio;

    public SeleccionAdicionalPedido(Long id, ItemPedido itemPedido, Adicional adicional, Double precio) {
        this.id = id;
        this.itemPedido = itemPedido;
        this.adicional = adicional;
        this.precio = precio;
    }
}
