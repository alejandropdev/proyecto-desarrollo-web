package com.muk.service.impl;

import com.muk.entities.Cliente;
import com.muk.repository.ClienteRepository;
import com.muk.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de clientes. Delega al repositorio.
 */
@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository repository;

    @Override
    public List<Cliente> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Cliente> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    @Override
    public Cliente save(Cliente cliente) {
        if (cliente == null) return null;
        return repository.save(cliente);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public Optional<Cliente> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<Cliente> findByEmailAndPassword(String email, String password) {
        return repository.findByEmailAndPassword(email, password);
    }

    @Override
    public Cliente registro(Cliente cliente) {
        if (cliente == null) return null;
        return repository.save(cliente);
    }
}
