package com.muk.service;

import com.muk.entities.Operador;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de operadores. Lógica de negocio.
 */
public interface OperadorService {

    List<Operador> findAll();

    Optional<Operador> findById(Long id);

    Operador save(Operador operador);

    void delete(Long id);

    Optional<Operador> findByUsuario(String usuario);

    Optional<Operador> findByUsuarioAndContrasena(String usuario, String contrasena);
}
