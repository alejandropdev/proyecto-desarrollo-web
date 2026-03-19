package com.muk.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "administradores")
@Getter
@Setter
@NoArgsConstructor
public class Administrador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(nullable = false, length = 100, name = "contraseña_hash")
    private String contrasenaHash;

    public Administrador(Long id, String usuario, String contrasenaHash) {
        this.id = id;
        this.usuario = usuario;
        this.contrasenaHash = contrasenaHash;
    }
}
