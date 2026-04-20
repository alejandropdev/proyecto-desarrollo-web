package com.muk.service.impl;

import com.muk.entities.Operador;
import com.muk.repository.OperadorRepository;
import com.muk.service.OperadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperadorServiceImpl implements OperadorService {

    private final OperadorRepository repository;

    @Autowired
    public OperadorServiceImpl(OperadorRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Operador> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Operador> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    @Override
    public Operador save(Operador operador) {
        if (operador == null) return null;
        return repository.save(operador);
    }

    @Override
    public void delete(Long id) {
        if (id != null) {
            findById(id).ifPresent(operador -> {
                operador.setActivo(false);
                repository.save(operador);
            });
        }
    }

    @Override
    public Optional<Operador> findByUsuario(String usuario) {
        return repository.findByUsuario(usuario);
    }

    @Override
    public Optional<Operador> findByUsuarioAndContrasena(String usuario, String contrasena) {
        return repository.findByUsuarioAndContrasenaHash(usuario, contrasena);
    }
}
