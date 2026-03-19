package com.muk.service.impl;

import com.muk.entities.Cliente;
import com.muk.repository.ClienteRepository;
import com.muk.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;

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
        return repository.findByEmailAndContrasenaHash(email, password);
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
        if (!password.equals(cliente.getContrasenaHash())) {
            return new LoginResult(null, "Credenciales inválidas.");
        }

        return new LoginResult(cliente, null);
    }

    @Override
    public Cliente registro(Cliente cliente) {
        if (cliente == null) return null;
        return repository.save(cliente);
    }

    @Override
    public RegistroResult registrarConValidacion(Cliente cliente) {
        if (cliente == null) {
            return new RegistroResult(null, "Datos inválidos.");
        }
        if (cliente.getEmail() == null || cliente.getEmail().isEmpty()) {
            return new RegistroResult(null, "El email es requerido.");
        }
        if (cliente.getContrasenaHash() == null || cliente.getContrasenaHash().isEmpty()) {
            return new RegistroResult(null, "La contraseña es requerida.");
        }
        if (repository.findByEmail(cliente.getEmail()).isPresent()) {
            return new RegistroResult(null, "El email ya está registrado.");
        }

        return new RegistroResult(repository.save(cliente), null);
    }

    @Override
    public PerfilResult obtenerPerfilPorEmail(String email, String missingEmailMessage) {
        if (email == null || email.isBlank()) {
            return new PerfilResult(null, missingEmailMessage);
        }
        return repository.findByEmail(email.trim())
                .map(cliente -> new PerfilResult(cliente, null))
                .orElseGet(() -> new PerfilResult(null, "Usuario no encontrado."));
    }

    @Override
    public ActionResult actualizarPerfil(Cliente cliente) {
        Long clienteId = (cliente == null) ? null : cliente.getId();
        if (cliente == null || clienteId == null) {
            return new ActionResult(false, "Datos inválidos.");
        }

        if (cliente.getContrasenaHash() == null || cliente.getContrasenaHash().isBlank()) {
            repository.findById(clienteId).ifPresent(existing ->
                    cliente.setContrasenaHash(existing.getContrasenaHash()));
        }

        repository.save(cliente);
        return new ActionResult(true, null);
    }

    @Override
    public ActionResult eliminarPerfilPorEmail(String email, String missingEmailMessage) {
        if (email == null || email.isBlank()) {
            return new ActionResult(false, missingEmailMessage);
        }

        Optional<Cliente> cliente = repository.findByEmail(email.trim());
        if (cliente.isEmpty()) {
            return new ActionResult(false, "Usuario no encontrado.");
        }

        Long clienteId = cliente.get().getId();
        if (clienteId == null) {
            return new ActionResult(false, "Datos inválidos.");
        }

        repository.deleteById(clienteId);
        return new ActionResult(true, null);
    }
}
