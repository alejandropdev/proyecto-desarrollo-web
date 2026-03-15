package com.muk.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "seleccion_adicional")
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

    public SeleccionAdicional() {
    }

    public SeleccionAdicional(Long id, ItemCarrito itemCarrito, Adicional adicional, Double precio) {
        this.id = id;
        this.itemCarrito = itemCarrito;
        this.adicional = adicional;
        this.precio = precio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemCarrito getItemCarrito() {
        return itemCarrito;
    }

    public void setItemCarrito(ItemCarrito itemCarrito) {
        this.itemCarrito = itemCarrito;
    }

    public Adicional getAdicional() {
        return adicional;
    }

    public void setAdicional(Adicional adicional) {
        this.adicional = adicional;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
