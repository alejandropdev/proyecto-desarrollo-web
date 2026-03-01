package com.muk.repository;

import com.muk.entities.Cliente;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Repositorio para el CRUD de clientes. Persistencia en memoria.
 */
@Repository
public class ClienteRepository {

    private final Map<Long, Cliente> db = new HashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public ClienteRepository() {
        seedData().forEach(c -> db.put(c.getId(), c));
        if (!db.isEmpty()) {
            nextId.set(Collections.max(db.keySet()) + 1);
        }
    }

    public List<Cliente> findAll() {
        return new ArrayList<>(db.values());
    }

    public Optional<Cliente> findById(Long id) {
        return Optional.ofNullable(db.get(id));
    }

    public Optional<Cliente> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return db.values().stream()
                .filter(c -> email.equals(c.getEmail()))
                .findFirst();
    }

    /**
     * Alternativa que busca por email y contraseña.
     */
    public Optional<Cliente> findByEmailAndPassword(String email, String password) {
        if (email == null || password == null) return Optional.empty();
        return db.values().stream()
                .filter(c -> email.equals(c.getEmail()) && password.equals(c.getPassword()))
                .findFirst();
    }

    /**
     * Crea (id == null) o actualiza. En creación asigna el siguiente id.
     */
    public Cliente save(Cliente c) {
        if (c == null) return null;
        if (c.getId() == null) {
            Long id = nextId.getAndIncrement();
            c.setId(id);
            db.put(id, c);
            return c;
        }
        db.put(c.getId(), c);
        return c;
    }

    public void delete(Long id) {
        if (id != null) db.remove(id);
    }

    private static List<Cliente> seedData() {
        return List.of(
                // contraseñas en texto plano para pruebas
                new Cliente(1L, "Gladis", "López", "gladis.lopez@example.com", "3001234567", "Calle 1 #2-3", "pass123"),
                new Cliente(2L, "Gloria", "Sánchez", "gloria.sanchez@example.com", "3109876543", "Carrera 4 #5-6", "qwerty")
        );
    }
}
