package com.muk.service.impl;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.*;
import com.muk.repository.*;
import com.muk.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final SeleccionAdicionalPedidoRepository seleccionAdicionalPedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final AdicionalRepository adicionalRepository;

    @Autowired
    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ItemPedidoRepository itemPedidoRepository,
            SeleccionAdicionalPedidoRepository seleccionAdicionalPedidoRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            AdicionalRepository adicionalRepository) {
        this.pedidoRepository = pedidoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.seleccionAdicionalPedidoRepository = seleccionAdicionalPedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.adicionalRepository = adicionalRepository;
    }

    @Override
    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    @Override
    public Optional<Pedido> findById(Long id) {
        return id == null ? Optional.empty() : pedidoRepository.findById(id);
    }

    @Override
    public List<Pedido> findByClienteId(Long clienteId) {
        if (clienteId == null) {
            return new ArrayList<>();
        }
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional
    public CrearPedidoResult crearPedido(Long clienteId, ApiDtos.CrearPedidoRequest request) {
        // Validar que el cliente exista
        if (clienteId == null) {
            return new CrearPedidoResult(null, "ID de cliente inválido.");
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            return new CrearPedidoResult(null, "Cliente no encontrado.");
        }

        // Validar que haya items
        if (request.items() == null || request.items().isEmpty()) {
            return new CrearPedidoResult(null, "El pedido debe contener al menos un producto.");
        }

        Cliente cliente = clienteOpt.get();

        // Crear el pedido con estado "PENDIENTE" por defecto
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setItems(new ArrayList<>());

        int totalProductos = 0;
        int totalAdiciones = 0;

        // Procesar cada item del pedido
        for (ApiDtos.ItemPedidoRequest itemRequest : request.items()) {
            // Validar que el producto exista
            Optional<Producto> productoOpt = productoRepository.findById(itemRequest.productoId());
            if (productoOpt.isEmpty()) {
                return new CrearPedidoResult(null, "Producto con ID " + itemRequest.productoId() + " no encontrado.");
            }

            Producto producto = productoOpt.get();

            // Crear item del pedido
            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProducto(producto);
            itemPedido.setCantidad(itemRequest.cantidad());
            itemPedido.setPrecioUnitario(producto.getPrecio());
            itemPedido.setSelecciones(new ArrayList<>());

            // Procesar adiciones si existen
            int adicionesEnItem = 0;
            if (itemRequest.adiciones() != null && !itemRequest.adiciones().isEmpty()) {
                for (ApiDtos.SeleccionAdicionalRequest adicionalRequest : itemRequest.adiciones()) {
                    Optional<Adicional> adicionalOpt = adicionalRepository.findById(adicionalRequest.adicionalId());
                    if (adicionalOpt.isPresent()) {
                        SeleccionAdicionalPedido seleccion = new SeleccionAdicionalPedido();
                        seleccion.setItemPedido(itemPedido);
                        seleccion.setAdicional(adicionalOpt.get());
                        seleccion.setPrecio(adicionalRequest.precio());
                        itemPedido.getSelecciones().add(seleccion);
                        adicionesEnItem++;
                    }
                }
            }

            pedido.getItems().add(itemPedido);
            totalProductos += itemRequest.cantidad();
            totalAdiciones += adicionesEnItem * itemRequest.cantidad();
        }

        // Asignar totales calculados
        pedido.setCantidadProductos(totalProductos);
        pedido.setCantidadAdiciones(totalAdiciones);

        // Guardar el pedido (cascada guardará items y selecciones)
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        return new CrearPedidoResult(pedidoGuardado, null);
    }

    @Override
    public Pedido save(Pedido pedido) {
        if (pedido == null) return null;
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id != null) {
            pedidoRepository.deleteById(id);
        }
    }

    @Override
    public List<Pedido> findByEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            return new ArrayList<>();
        }
        return pedidoRepository.findByEstado(estado);
    }
}
