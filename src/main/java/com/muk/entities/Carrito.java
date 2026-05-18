package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false, unique = true)
    private Cliente cliente;

    @Column(nullable = false)
    private LocalDateTime ultimaActualizacion = LocalDateTime.now();

    @JsonIgnore
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items;

    public Carrito(Long id, Cliente cliente) {
        this.id = id;
        this.cliente = cliente;
        this.ultimaActualizacion = LocalDateTime.now();
    }
}
