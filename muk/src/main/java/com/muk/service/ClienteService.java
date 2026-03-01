package com.muk.service;

import com.muk.entities.Cliente;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del servicio de clientes.
 */
public interface ClienteService {

    List<Cliente> findAll();

    Optional<Cliente> findById(Long id);

    Cliente save(Cliente cliente);

    void delete(Long id);

    /**
     * Busca un cliente por email para login.
     */
    Optional<Cliente> findByEmail(String email);

    /**
     * Busca un cliente cuyo email y contraseña coinciden (texto plano).
     */
    Optional<Cliente> findByEmailAndPassword(String email, String password);

    /**
     * Registra un nuevo cliente.
     */
    Cliente registro(Cliente cliente);
}
