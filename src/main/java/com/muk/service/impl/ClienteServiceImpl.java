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
        System.out.println("[ClienteServiceImpl.actualizarPerfil] cliente=" + (cliente != null ? "OK" : "NULL") + ", id=" + clienteId);
        
        if (cliente == null || clienteId == null) {
            System.out.println("[ClienteServiceImpl] ERROR: Cliente es null o ID es null");
            return new ActionResult(false, "Datos inválidos.");
        }

        // Validar campos requeridos
        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            return new ActionResult(false, "El nombre es requerido.");
        }
        if (cliente.getApellido() == null || cliente.getApellido().isBlank()) {
            return new ActionResult(false, "El apellido es requerido.");
        }
        if (cliente.getEmail() == null || cliente.getEmail().isBlank()) {
            return new ActionResult(false, "El email es requerido.");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            return new ActionResult(false, "El teléfono es requerido.");
        }
        if (cliente.getDireccion() == null || cliente.getDireccion().isBlank()) {
            return new ActionResult(false, "La dirección es requerida.");
        }

        try {
            // Cargar el cliente existente de la BD para preservar las relaciones
            System.out.println("[ClienteServiceImpl] Cargando cliente existente de la BD...");
            Optional<Cliente> existingOpt = repository.findById(clienteId);
            if (existingOpt.isEmpty()) {
                return new ActionResult(false, "Cliente no encontrado en la base de datos.");
            }
            
            Cliente existing = existingOpt.get();
            System.out.println("[ClienteServiceImpl] Cliente existente cargado. Actualizando campos...");
            
            // Actualizar SOLO los campos que el usuario modificó
            existing.setNombre(cliente.getNombre());
            existing.setApellido(cliente.getApellido());
            existing.setEmail(cliente.getEmail());
            existing.setTelefono(cliente.getTelefono());
            existing.setDireccion(cliente.getDireccion());
            
            // Manejar contraseña - solo actualizar si se proporcionó una nueva
            if (cliente.getContrasenaHash() != null && !cliente.getContrasenaHash().isBlank()) {
                System.out.println("[ClienteServiceImpl] Actualizando contraseña");
                existing.setContrasenaHash(cliente.getContrasenaHash());
            }

            // Las relaciones (pedidos, carrito) se preservan automáticamente
            System.out.println("[ClienteServiceImpl] Guardando cliente actualizado con ID: " + clienteId);
            repository.save(existing);
            System.out.println("[ClienteServiceImpl] ✓ Cliente guardado exitosamente");
            return new ActionResult(true, null);
            
        } catch (Exception e) {
            System.out.println("[ClienteServiceImpl] ERROR al guardar: " + e.getMessage());
            e.printStackTrace();
            return new ActionResult(false, "Error al guardar los cambios: " + e.getMessage());
        }
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
