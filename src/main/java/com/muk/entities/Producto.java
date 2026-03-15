package com.muk.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "imagen_url", nullable = false, length = 255)
    private String imagenUrl;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany
    @JoinTable(
            name = "producto_adicional",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "adicional_id")
    )
    private List<Adicional> adicionalesPermitidos;

    public Producto() {
        this.categoria = new Categoria();
        this.activo = true;
    }

    public Producto(Long id, String nombre, Categoria categoria, Double precio, String imagenUrl, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
        this.descripcion = descripcion;
        this.activo = true;
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

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public List<Adicional> getAdicionalesPermitidos() {
        return adicionalesPermitidos;
    }

    public void setAdicionalesPermitidos(List<Adicional> adicionalesPermitidos) {
        this.adicionalesPermitidos = adicionalesPermitidos;
    }
}