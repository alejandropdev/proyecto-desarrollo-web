package com.muk.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "domiciliarios")
public class Domiciliario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String celular;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(nullable = false)
    private Boolean disponible = true;

    @OneToMany(mappedBy = "domiciliario")
    private List<Pedido> pedidosAsignados;

    public Domiciliario() {
    }

    public Domiciliario(Long id, String nombre, String celular, String cedula) {
        this.id = id;
        this.nombre = nombre;
        this.celular = celular;
        this.cedula = cedula;
        this.disponible = true;
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

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public List<Pedido> getPedidosAsignados() {
        return pedidosAsignados;
    }

    public void setPedidosAsignados(List<Pedido> pedidosAsignados) {
        this.pedidosAsignados = pedidosAsignados;
    }
}
