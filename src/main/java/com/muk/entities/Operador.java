package com.muk.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "operadores")
public class Operador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(nullable = false, length = 100, name = "contraseña_hash")
    private String contrasenaHash;

    @OneToMany(mappedBy = "operador")
    private List<Pedido> pedidosGestionados;

    public Operador() {
    }

    public Operador(Long id, String nombre, String usuario, String contrasenaHash) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenaHash = contrasenaHash;
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

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        this.contrasenaHash = contrasenaHash;
    }

    public List<Pedido> getPedidosGestionados() {
        return pedidosGestionados;
    }

    public void setPedidosGestionados(List<Pedido> pedidosGestionados) {
        this.pedidosGestionados = pedidosGestionados;
    }
}
