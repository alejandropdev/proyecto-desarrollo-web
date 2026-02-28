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
}
