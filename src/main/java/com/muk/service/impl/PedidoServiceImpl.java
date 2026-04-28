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
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final AdicionalRepository adicionalRepository;
    private final DomiciliarioRepository domiciliarioRepository;

    @Autowired
    public PedidoServiceImpl(
            PedidoRepository pedidoRepository,
            ItemPedidoRepository itemPedidoRepository,
            SeleccionAdicionalPedidoRepository seleccionAdicionalPedidoRepository,
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            AdicionalRepository adicionalRepository,
            DomiciliarioRepository domiciliarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.adicionalRepository = adicionalRepository;
        this.domiciliarioRepository = domiciliarioRepository;
    }

    public List<Pedido> findAll() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        return id == null ? Optional.empty() : pedidoRepository.findById(id);
    }

    public List<Pedido> findByClienteId(Long clienteId) {
        if (clienteId == null) return new ArrayList<>();
        return pedidoRepository.findByClienteId(clienteId);
    }

    @Transactional
    public CrearPedidoResult crearPedido(Long clienteId, ApiDtos.CrearPedidoRequest request) {
        if (clienteId == null) {
            return new CrearPedidoResult(null, "ID de cliente inválido.");
        }

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);

        if (clienteOpt.isEmpty()) {
            return new CrearPedidoResult(null, "Cliente no encontrado.");
        }

        if (request.items() == null || request.items().isEmpty()) {
            return new CrearPedidoResult(null, "El pedido debe contener al menos un producto.");
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(clienteOpt.get());
        pedido.setEstado("PENDIENTE");
        pedido.setFechaCreacion(LocalDateTime.now());
        pedido.setItems(new ArrayList<>());

        int totalProductos = 0;
        int totalAdiciones = 0;

        for (ApiDtos.ItemPedidoRequest itemRequest : request.items()) {
            Optional<Producto> productoOpt = productoRepository.findById(itemRequest.productoId());

            if (productoOpt.isEmpty()) {
                return new CrearPedidoResult(null, "Producto no encontrado.");
            }

            Producto producto = productoOpt.get();

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProducto(producto);
            itemPedido.setCantidad(itemRequest.cantidad());
            itemPedido.setPrecioUnitario(producto.getPrecio());
            itemPedido.setSelecciones(new ArrayList<>());

            int adicionesEnItem = 0;

            if (itemRequest.adiciones() != null) {
                for (ApiDtos.SeleccionAdicionalRequest adicionalRequest : itemRequest.adiciones()) {
                    Optional<Adicional> adicionalOpt = adicionalRepository.findById(adicionalRequest.adicionalId());

                    if (adicionalOpt.isPresent()) {
                        Adicional adicional = adicionalOpt.get();
                        SeleccionAdicionalPedido seleccion = new SeleccionAdicionalPedido();
                        seleccion.setItemPedido(itemPedido);
                        seleccion.setAdicional(adicional);
                        seleccion.setPrecio(adicional.getPrecio());

                        itemPedido.getSelecciones().add(seleccion);
                        adicionesEnItem++;
                    }
                }
            }

            pedido.getItems().add(itemPedido);
            totalProductos += itemRequest.cantidad();
            totalAdiciones += adicionesEnItem * itemRequest.cantidad();
        }

        pedido.setCantidadProductos(totalProductos);
        pedido.setCantidadAdiciones(totalAdiciones);

        return new CrearPedidoResult(pedidoRepository.save(pedido), null);
    }

    public Pedido save(Pedido pedido) {
        if (pedido == null) return null;
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void delete(Long id) {
        if (id != null) {
            pedidoRepository.deleteById(id);
        }
    }

    public List<Pedido> findByEstado(String estado) {
        if (estado == null || estado.isBlank()) return new ArrayList<>();
        return pedidoRepository.findByEstado(estado);
    }

    public List<Pedido> findPedidosNoCompletados() {
        return pedidoRepository.findAll()
                .stream()
                .filter(p -> !"COMPLETADO".equalsIgnoreCase(p.getEstado()))
                .filter(p -> !"CANCELADO".equalsIgnoreCase(p.getEstado()))
                .toList();
    }

    public List<Pedido> findByProductoId(Long productoId) {
        if (productoId == null) return new ArrayList<>();
        return pedidoRepository.findByProductoId(productoId);
    }

    @Transactional
    public CambiarEstadoResult cambiarEstado(Long pedidoId, String nuevoEstado) {
        if (pedidoId == null) {
            return new CambiarEstadoResult(null, "ID de pedido inválido.");
        }

        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return new CambiarEstadoResult(null, "Estado inválido.");
        }

        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);

        if (pedidoOpt.isEmpty()) {
            return new CambiarEstadoResult(null, "Pedido no encontrado.");
        }

        Pedido pedido = pedidoOpt.get();
        String estadoFinal = normalizarEstado(nuevoEstado);

        if (!estadoValido(estadoFinal)) {
            return new CambiarEstadoResult(null, "Estado no válido: " + nuevoEstado);
        }

        if ("EN_CAMINO".equals(estadoFinal)) {
            if (pedido.getDomiciliario() == null) {
                Optional<Domiciliario> disponible = domiciliarioRepository.findByDisponibleTrue()
                        .stream()
                        .filter(d -> d.getActivo() == null || Boolean.TRUE.equals(d.getActivo()))
                        .findFirst();

                if (disponible.isEmpty()) {
                    return new CambiarEstadoResult(null, "No hay domiciliarios disponibles.");
                }

                Domiciliario domiciliario = disponible.get();
                domiciliario.setDisponible(false);
                domiciliarioRepository.save(domiciliario);

                pedido.setDomiciliario(domiciliario);
            } else {
                Domiciliario domiciliario = pedido.getDomiciliario();
                domiciliario.setDisponible(false);
                domiciliarioRepository.save(domiciliario);
            }
        }

        if ("COMPLETADO".equals(estadoFinal)) {
            pedido.setFechaEntrega(LocalDateTime.now());
            liberarDomiciliario(pedido);
        }

        if ("CANCELADO".equals(estadoFinal)) {
            liberarDomiciliario(pedido);
        }

        pedido.setEstado(estadoFinal);

        return new CambiarEstadoResult(pedidoRepository.save(pedido), null);
    }

    @Transactional
    public AsignarDomiciliarioResult asignarDomiciliario(Long pedidoId, Long domiciliarioId) {
        if (pedidoId == null || domiciliarioId == null) {
            return new AsignarDomiciliarioResult(null, "Datos inválidos.");
        }

        Optional<Pedido> pedidoOpt = pedidoRepository.findById(pedidoId);
        Optional<Domiciliario> domiciliarioOpt = domiciliarioRepository.findById(domiciliarioId);

        if (pedidoOpt.isEmpty()) {
            return new AsignarDomiciliarioResult(null, "Pedido no encontrado.");
        }

        if (domiciliarioOpt.isEmpty()) {
            return new AsignarDomiciliarioResult(null, "Domiciliario no encontrado.");
        }

        Domiciliario domiciliario = domiciliarioOpt.get();

        if (domiciliario.getActivo() != null && !Boolean.TRUE.equals(domiciliario.getActivo())) {
            return new AsignarDomiciliarioResult(null, "El domiciliario no está activo.");
        }

        if (!Boolean.TRUE.equals(domiciliario.getDisponible())) {
            return new AsignarDomiciliarioResult(null, "El domiciliario no está disponible.");
        }

        Pedido pedido = pedidoOpt.get();
        pedido.setDomiciliario(domiciliario);

        return new AsignarDomiciliarioResult(pedidoRepository.save(pedido), null);
    }

    private void liberarDomiciliario(Pedido pedido) {
        if (pedido.getDomiciliario() != null) {
            Domiciliario domiciliario = pedido.getDomiciliario();
            domiciliario.setDisponible(true);
            domiciliarioRepository.save(domiciliario);
        }
    }

    private boolean estadoValido(String estado) {
        return List.of(
                "PENDIENTE",
                "EN_PREPARACION",
                "LISTO",
                "EN_CAMINO",
                "COMPLETADO",
                "CANCELADO"
        ).contains(estado);
    }

    private String normalizarEstado(String estado) {
        if ("ENTREGADO".equalsIgnoreCase(estado)) {
            return "COMPLETADO";
        }

        return estado.trim().toUpperCase();
    }
}