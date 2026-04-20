package com.muk.service.impl;

import com.muk.entities.Pedido;
import com.muk.repository.PedidoRepository;
import com.muk.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository repository;

    @Autowired
    public PedidoServiceImpl(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Pedido> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return id == null ? Optional.empty() : repository.findById(id);
    }

    @Override
    public Pedido save(Pedido pedido) {
        if (pedido == null) return null;
        return repository.save(pedido);
    }

    @Override
    public void delete(Long id) {
        if (id != null) {
            repository.deleteById(id);
        }
    }

    @Override
    public List<Pedido> findByOperadorId(Long operadorId) {
        return operadorId == null ? List.of() : repository.findByOperadorId(operadorId);
    }

    @Override
    public List<Pedido> findByClienteId(Long clienteId) {
        return clienteId == null ? List.of() : repository.findByClienteId(clienteId);
    }

    @Override
    public List<Pedido> findByEstado(String estado) {
        return estado == null ? List.of() : repository.findByEstado(estado);
    }
}
