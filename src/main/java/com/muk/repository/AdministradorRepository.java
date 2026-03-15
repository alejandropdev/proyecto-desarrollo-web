package com.muk.repository;

import com.muk.entities.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {

    Optional<Administrador> findByUsuario(String usuario);

    Optional<Administrador> findByUsuarioAndContrasenaHash(String usuario, String contrasenaHash);
}
