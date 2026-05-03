package com.muk.service;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Cliente;
import com.muk.entities.Pedido;
import com.muk.entities.Producto;
import com.muk.repository.ClienteRepository;
import com.muk.repository.PedidoRepository;
import com.muk.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import com.muk.entities.Categoria;
import com.muk.repository.CategoriaRepository;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PedidoServiceIntegrationTest {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Cliente clienteTest;
    private Producto productoTest;
    private Categoria categoriaTest;

    @BeforeEach
    void setUp() {
        // Crear datos reales en la BD de prueba
        Cliente cliente = new Cliente();
        cliente.setNombre("Test Integration");
        cliente.setApellido("Perez");
        cliente.setEmail("test.integration@example.com");
        cliente.setContrasenaHash("hashedpassword123");
        cliente.setTelefono("9999999999");
        cliente.setDireccion("Avenida Siempre Viva");
        clienteTest = clienteRepository.save(cliente);

        Categoria categoria = new Categoria();
        categoria.setNombre("Comida Rapida");
        categoriaTest = categoriaRepository.save(categoria);

        Producto producto = new Producto();
        producto.setNombre("Pizza Test");
        producto.setCategoria(categoriaTest);
        producto.setPrecio(20000.0);
        producto.setImagenUrl("http://example.com/pizza.jpg");
        producto.setDescripcion("Una pizza deliciosa");
        producto.setActivo(true);
        productoTest = productoRepository.save(producto);
    }

    @Test
    void crearPedido_IntegracionExitosa() {
        // Arrange
        ApiDtos.ItemPedidoRequest itemRequest = new ApiDtos.ItemPedidoRequest(productoTest.getId(), 3, new ArrayList<>());
        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(List.of(itemRequest));

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteTest.getId(), request);

        // Assert
        assertTrue(result.success());
        assertNotNull(result.pedido().getId());
        assertEquals("PENDIENTE", result.pedido().getEstado());
        assertEquals(3, result.pedido().getCantidadProductos());

        // Verificar que realmente se guardó en la base de datos
        Optional<Pedido> pedidoGuardado = pedidoRepository.findById(result.pedido().getId());
        assertTrue(pedidoGuardado.isPresent());
        assertEquals(clienteTest.getId(), pedidoGuardado.get().getCliente().getId());
        assertEquals(1, pedidoGuardado.get().getItems().size());
        assertEquals(productoTest.getId(), pedidoGuardado.get().getItems().get(0).getProducto().getId());
        assertEquals(3, pedidoGuardado.get().getItems().get(0).getCantidad());
    }

    @Test
    void cambiarEstado_IntegracionDePendienteAPreparacion() {
        // Arrange: Crear un pedido directamente
        Pedido pedido = new Pedido();
        pedido.setCliente(clienteTest);
        pedido.setEstado("PENDIENTE");
        pedido.setCantidadProductos(1);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // Act: Cambiar el estado a EN_PREPARACION
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(pedidoGuardado.getId(), "EN_PREPARACION");

        // Assert
        assertTrue(result.success());
        assertEquals("EN_PREPARACION", result.pedido().getEstado());

        // Verificar la persistencia en DB
        Optional<Pedido> pedidoEnBD = pedidoRepository.findById(pedidoGuardado.getId());
        assertTrue(pedidoEnBD.isPresent());
        assertEquals("EN_PREPARACION", pedidoEnBD.get().getEstado());
    }
}
