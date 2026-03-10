package com.muk.service.impl;

import com.muk.entities.Cliente;
import com.muk.repository.ClienteRepository;
import com.muk.service.ClienteService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

    public ClienteServiceImpl(ClienteRepository repository) {
        this.repository = repository;
    }

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
        if (id != null) {
            repository.deleteById(id);
        }
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
    public LoginResult login(String email, String password) {
        if (email == null || email.isBlank()) {
            return new LoginResult(null, "Email no registrado.");
        }
        if (password == null || password.isBlank()) {
            return new LoginResult(null, "Credenciales inválidas.");
        }

        Optional<Cliente> byEmail = repository.findByEmail(email.trim());
        if (byEmail.isEmpty()) {
            return new LoginResult(null, "Email no registrado.");
        }

        Cliente cliente = byEmail.get();
        if (!password.equals(cliente.getPassword())) {
            return new LoginResult(null, "Credenciales inválidas.");
        }

        return new LoginResult(cliente, null);
    }

    @Override
    public Cliente registro(Cliente cliente) {
        if (cliente == null) return null;
        return repository.save(cliente);
    }
}
