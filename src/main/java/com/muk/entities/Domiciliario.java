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

    /**
     * Indica si el domiciliario está activo en el sistema.
     * Un domiciliario inactivo no puede ser asignado a nuevos pedidos.
     */
    @Column(nullable = false)
    private Boolean activo = true;

    /**
     * Indica si el domiciliario está disponible para recibir pedidos.
     * Se modifica automáticamente según el estado de los pedidos asignados:
     * - false cuando el pedido está EN_CAMINO
     * - true cuando el pedido está COMPLETADO
     */
    @Column(nullable = false)
    private Boolean disponible = true;

    @OneToMany(mappedBy = "domiciliario")
    private List<Pedido> pedidosAsignados;

    /**
     * Constructor que inicializa los campos básicos.
     * Por defecto, activo=true y disponible=true.
     */
    public Domiciliario(Long id, String nombre, String celular, String cedula) {
        this.id = id;
        this.nombre = nombre;
        this.celular = celular;
        this.cedula = cedula;
        this.activo = true;
        this.disponible = true;
    }
}
