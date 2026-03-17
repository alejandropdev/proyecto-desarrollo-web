package com.muk.repository;

import com.muk.entities.Operador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OperadorRepository extends JpaRepository<Operador, Long> {

    Optional<Operador> findByUsuario(String usuario);

    Optional<Operador> findByUsuarioAndContrasenaHash(String usuario, String contrasenaHash);
}
