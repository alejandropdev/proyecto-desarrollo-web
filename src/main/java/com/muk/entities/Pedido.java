package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operador_id", nullable = true)
    private Operador operador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domiciliario_id", nullable = true)
    private Domiciliario domiciliario;

    @Column(nullable = false, length = 30)
    private String estado = "PENDIENTE";

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(nullable = true)
    private LocalDateTime fechaEntrega;

    public Pedido(Long id, Cliente cliente, String estado, LocalDateTime fechaCreacion) {
        this.id = id;
        this.cliente = cliente;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }
}
