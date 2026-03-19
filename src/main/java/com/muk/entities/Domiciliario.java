package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "domiciliarios")
@Getter
@Setter
@NoArgsConstructor
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

    public Domiciliario(Long id, String nombre, String celular, String cedula) {
        this.id = id;
        this.nombre = nombre;
        this.celular = celular;
        this.cedula = cedula;
        this.disponible = true;
    }
}
