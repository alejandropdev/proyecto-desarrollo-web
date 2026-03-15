package com.muk.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categorias")
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

    public Categoria() {
    }

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Adicional> getAdiciones() {
        return adiciones;
    }

    public void setAdiciones(List<Adicional> adiciones) {
        this.adiciones = adiciones;
    }
}
