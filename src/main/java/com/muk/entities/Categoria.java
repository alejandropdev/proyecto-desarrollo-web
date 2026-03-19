package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;

    @OneToMany(mappedBy = "categoria")
    private List<Adicional> adiciones;

    public Categoria(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.description = null;
    }

    public Categoria(Long id, String nombre, String description) {
        this.id = id;
        this.nombre = nombre;
        this.description = description;
    }
}
