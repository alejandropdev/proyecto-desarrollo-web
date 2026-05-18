package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

@Entity
@Table(name = "adiciones")
@Getter
@Setter
@NoArgsConstructor
public class Adicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = true)
    private Categoria categoria;

    @JsonIgnore
    @ManyToMany(mappedBy = "adicionalesPermitidos")
    private List<Producto> productos;

    public Adicional(Long id, String nombre, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = 0.0;
        this.activo = true;
    }

    public Adicional(Long id, String nombre, Double precio, Categoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.activo = true;
    }
}
