package com.muk.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "operadores")
@Getter
@Setter
@NoArgsConstructor
public class Operador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Transient
    private String contrasenaHash;

    @Column(nullable = false)
    private Boolean activo = true;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @JsonIgnore
    @OneToMany(mappedBy = "operador")
    private List<Pedido> pedidosGestionados;

    public Operador(Long id, String nombre, String usuario, String contrasenaHash) {
        this.id = id;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenaHash = contrasenaHash;
    }
}
