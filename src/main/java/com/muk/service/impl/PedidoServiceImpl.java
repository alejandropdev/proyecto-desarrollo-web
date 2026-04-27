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
        this.itemPedidoRepository = itemPedidoRepository;
        this.seleccionAdicionalPedidoRepository = seleccionAdicionalPedidoRepository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.adicionalRepository = adicionalRepository;
        this.domiciliarioRepository = domiciliarioRepository;
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
            Optional<Producto> productoOpt = productoRepository.findById(Long.valueOf(itemRequest.productoId()));
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
                    Optional<Adicional> adicionalOpt = adicionalRepository.findById(Long.valueOf(adicionalRequest.adicionalId()));
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

    @Override
    public List<Pedido> findPedidosNoCompletados() {
        // Obtener pedidos que NO están en COMPLETADO ni CANCELADO
        List<Pedido> pedidosPendiente = pedidoRepository.findByEstado("PENDIENTE");
        List<Pedido> pedidosEnPreparacion = pedidoRepository.findByEstado("EN_PREPARACION");
        List<Pedido> pedidosListo = pedidoRepository.findByEstado("LISTO");
        List<Pedido> pedidosEnCamino = pedidoRepository.findByEstado("EN_CAMINO");

        List<Pedido> resultado = new ArrayList<>();
        resultado.addAll(pedidosPendiente);
        resultado.addAll(pedidosEnPreparacion);
        resultado.addAll(pedidosListo);
        resultado.addAll(pedidosEnCamino);

        return resultado;
    }

    @Override
    @Transactional
    public CambiarEstadoResult cambiarEstado(Long pedidoId, String nuevoEstado) {
        // Validar que el ID del pedido sea válido
        if (pedidoId == null || pedidoId <= 0) {
            return new CambiarEstadoResult(null, "ID de pedido inválido.");
        }

        // Validar que el nuevo estado sea válido
        if (nuevoEstado == null || nuevoEstado.isBlank()) {
            return new CambiarEstadoResult(null, "El estado del pedido no puede estar vacío.");
        }

        // Estados válidos
        List<String> estadosValidos = List.of("PENDIENTE", "EN_PREPARACION", "LISTO", "EN_CAMINO", "COMPLETADO", "CANCELADO");
        if (!estadosValidos.contains(nuevoEstado.toUpperCase())) {
            return new CambiarEstadoResult(null, "Estado '" + nuevoEstado + "' no es válido. Estados válidos: " + estadosValidos);
        }

        // Buscar el pedido
        Optional<Pedido> pedidoOpt = findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            return new CambiarEstadoResult(null, "Pedido con ID " + pedidoId + " no encontrado.");
        }

        Pedido pedido = pedidoOpt.get();

        // Lógica especial: Si cambio a EN_CAMINO sin domiciliario, asignar uno automáticamente
        if (nuevoEstado.equalsIgnoreCase("EN_CAMINO")) {
            if (pedido.getDomiciliario() == null) {
                // Buscar un domiciliario activo y disponible
                List<Domiciliario> disponibles = domiciliarioRepository.findAll()
                        .stream()
                        .filter(d -> Boolean.TRUE.equals(d.getActivo()) && Boolean.TRUE.equals(d.getDisponible()))
                        .toList();

                if (disponibles.isEmpty()) {
                    return new CambiarEstadoResult(null, "No hay domiciliarios disponibles para entregar el pedido.");
                }

                // Asignar el primer disponible
                Domiciliario asignado = disponibles.get(0);
                pedido.setDomiciliario(asignado);
            }
        }

        // Actualizar estado del pedido
        pedido.setEstado(nuevoEstado.toUpperCase());

        // Lógica especial: Gestionar disponibilidad del domiciliario
        if (pedido.getDomiciliario() != null) {
            Domiciliario domiciliario = pedido.getDomiciliario();

            // Si el pedido pasa a EN_CAMINO, marcar domiciliario como NO disponible
            if (nuevoEstado.equalsIgnoreCase("EN_CAMINO")) {
                domiciliario.setDisponible(false);
            }
            // Si el pedido llega a COMPLETADO, marcar domiciliario como disponible
            else if (nuevoEstado.equalsIgnoreCase("COMPLETADO")) {
                domiciliario.setDisponible(true);
                pedido.setFechaEntrega(LocalDateTime.now()); // Registrar fecha de entrega
            }
            // Si el pedido se CANCELA, marcar domiciliario como disponible nuevamente
            else if (nuevoEstado.equalsIgnoreCase("CANCELADO")) {
                domiciliario.setDisponible(true);
            }
        }

        // Guardar el pedido actualizado
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        return new CambiarEstadoResult(pedidoActualizado, null);
    }

    @Override
    @Transactional
    public AsignarDomiciliarioResult asignarDomiciliario(Long pedidoId, Long domiciliarioId) {
        // Validar parámetros
        if (pedidoId == null || pedidoId <= 0) {
            return new AsignarDomiciliarioResult(null, "ID de pedido inválido.");
        }
        if (domiciliarioId == null || domiciliarioId <= 0) {
            return new AsignarDomiciliarioResult(null, "ID de domiciliario inválido.");
        }

        // Buscar el pedido
        Optional<Pedido> pedidoOpt = findById(pedidoId);
        if (pedidoOpt.isEmpty()) {
            return new AsignarDomiciliarioResult(null, "Pedido con ID " + pedidoId + " no encontrado.");
        }

        // Buscar el domiciliario
        Optional<Domiciliario> domiciliarioOpt = domiciliarioRepository.findById(domiciliarioId);
        if (domiciliarioOpt.isEmpty()) {
            return new AsignarDomiciliarioResult(null, "Domiciliario con ID " + domiciliarioId + " no encontrado.");
        }

        Domiciliario domiciliario = domiciliarioOpt.get();

        // Verificar que el domiciliario esté activo y disponible
        if (!domiciliario.getActivo()) {
            return new AsignarDomiciliarioResult(null, "El domiciliario está desactivado.");
        }
        if (!domiciliario.getDisponible()) {
            return new AsignarDomiciliarioResult(null, "El domiciliario no está disponible actualmente.");
        }

        Pedido pedido = pedidoOpt.get();
        
        // Asignar el domiciliario al pedido
        pedido.setDomiciliario(domiciliario);
        
        // Guardar el pedido actualizado
        Pedido pedidoActualizado = pedidoRepository.save(pedido);

        return new AsignarDomiciliarioResult(pedidoActualizado, null);
    }
}
