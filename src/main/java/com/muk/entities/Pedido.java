package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = true)
    private Operador operador;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domiciliario_id", nullable = true)
    private Domiciliario domiciliario;

    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime fechaEntrega;

    @Column(nullable = false)
    private Integer cantidadProductos = 0;

    @Column(nullable = false)
    private Integer cantidadAdiciones = 0;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> items;

    public Pedido(Long id, Cliente cliente, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.cliente = cliente;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.cantidadProductos = 0;
        this.cantidadAdiciones = 0;
    }
}
