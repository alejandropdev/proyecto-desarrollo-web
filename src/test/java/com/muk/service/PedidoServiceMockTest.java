package com.muk.service;

import com.muk.controller.api.ApiDtos;
import com.muk.entities.Adicional;
import com.muk.entities.Cliente;
import com.muk.entities.Domiciliario;
import com.muk.entities.Pedido;
import com.muk.entities.Producto;
import com.muk.repository.AdicionalRepository;
import com.muk.repository.ClienteRepository;
import com.muk.repository.DomiciliarioRepository;
import com.muk.repository.ItemPedidoRepository;
import com.muk.repository.PedidoRepository;
import com.muk.repository.ProductoRepository;
import com.muk.repository.SeleccionAdicionalPedidoRepository;
import com.muk.service.impl.PedidoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceMockTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AdicionalRepository adicionalRepository;

    @Mock
    private DomiciliarioRepository domiciliarioRepository;

    @Mock
    private ItemPedidoRepository itemPedidoRepository;

    @Mock
    private SeleccionAdicionalPedidoRepository seleccionAdicionalPedidoRepository;

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    private Cliente clienteMock;
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        clienteMock = new Cliente();
        clienteMock.setId(1L);
        clienteMock.setNombre("Juan Perez");
        clienteMock.setTelefono("1234567890");
        clienteMock.setDireccion("Calle Falsa 123");

        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Hamburguesa");
        productoMock.setPrecio(15000.0);
    }

    @Test
    void crearPedido_Exitoso() {
        // Arrange
        Long clienteId = 1L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setCliente(clienteMock);
        pedidoGuardado.setEstado("PENDIENTE");
        pedidoGuardado.setCantidadProductos(2);

        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        ApiDtos.ItemPedidoRequest itemRequest = new ApiDtos.ItemPedidoRequest(1L, 2, new ArrayList<>());
        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(List.of(itemRequest));

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        // Assert
        assertTrue(result.success());
        assertNotNull(result.pedido());
        assertEquals("PENDIENTE", result.pedido().getEstado());
        assertEquals(1L, result.pedido().getId());
        assertNull(result.errorMessage());

        // Verification
        verify(clienteRepository).findById(clienteId);
        verify(productoRepository).findById(1L);
        verify(pedidoRepository).save(any(Pedido.class));
    }

    @Test
    void crearPedido_ClienteNoEncontrado() {
        // Arrange
        Long clienteId = 99L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(new ArrayList<>());

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        // Assert
        assertFalse(result.success());
        assertEquals("Cliente no encontrado.", result.errorMessage());
        assertNull(result.pedido());

        verify(clienteRepository).findById(clienteId);
        verifyNoInteractions(productoRepository);
        verifyNoInteractions(pedidoRepository);
    }

    @Test
    void cambiarEstado_EnCaminoAsignaDomiciliario() {
        // Arrange
        Long pedidoId = 1L;
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(pedidoId);
        pedidoExistente.setEstado("LISTO");

        Domiciliario domiciliarioDisponible = new Domiciliario();
        domiciliarioDisponible.setId(1L);
        domiciliarioDisponible.setDisponible(true);
        domiciliarioDisponible.setActivo(true);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoExistente));
        when(domiciliarioRepository.findByDisponibleTrue()).thenReturn(List.of(domiciliarioDisponible));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(pedidoId, "EN_CAMINO");

        // Assert
        assertTrue(result.success());
        assertEquals("EN_CAMINO", result.pedido().getEstado());
        assertNotNull(result.pedido().getDomiciliario());
        assertEquals(1L, result.pedido().getDomiciliario().getId());
        assertFalse(domiciliarioDisponible.getDisponible()); // Se marca como no disponible

        verify(pedidoRepository).findById(pedidoId);
        verify(domiciliarioRepository).findByDisponibleTrue();
        verify(domiciliarioRepository).save(domiciliarioDisponible);
        verify(pedidoRepository).save(pedidoExistente);
    }

    @Test
    void cambiarEstado_NoHayDomiciliarioDisponible() {
        // Arrange
        Long pedidoId = 1L;
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(pedidoId);
        pedidoExistente.setEstado("LISTO");

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedidoExistente));
        when(domiciliarioRepository.findByDisponibleTrue()).thenReturn(Collections.emptyList());

        // Act
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(pedidoId, "EN_CAMINO");

        // Assert
        assertFalse(result.success());
        assertEquals("No hay domiciliarios disponibles.", result.errorMessage());
        assertNull(result.pedido());

        verify(pedidoRepository).findById(pedidoId);
        verify(domiciliarioRepository).findByDisponibleTrue();
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    // =========================================================================
    // crearPedido — casos adicionales
    // =========================================================================

    @Test
    void crearPedido_ClienteIdNulo_Falla() {
        // Arrange
        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(new ArrayList<>());

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(null, request);

        // Assert
        assertFalse(result.success());
        assertEquals("ID de cliente inválido.", result.errorMessage());
        assertNull(result.pedido());

        verifyNoInteractions(clienteRepository);
        verifyNoInteractions(pedidoRepository);
    }

    @Test
    void crearPedido_ItemsVacios_Falla() {
        // Arrange
        Long clienteId = 1L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));

        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(new ArrayList<>());

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        // Assert
        assertFalse(result.success());
        assertEquals("El pedido debe contener al menos un producto.", result.errorMessage());
        assertNull(result.pedido());

        verify(clienteRepository).findById(clienteId);
        verifyNoInteractions(pedidoRepository);
    }

    @Test
    void crearPedido_ProductoNoEncontrado_Falla() {
        // Arrange
        Long clienteId = 1L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        ApiDtos.ItemPedidoRequest itemRequest = new ApiDtos.ItemPedidoRequest(99L, 1, new ArrayList<>());
        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(List.of(itemRequest));

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        // Assert
        assertFalse(result.success());
        assertEquals("Producto no encontrado.", result.errorMessage());
        assertNull(result.pedido());

        verify(clienteRepository).findById(clienteId);
        verify(productoRepository).findById(99L);
        verifyNoInteractions(pedidoRepository);
    }

    @Test
    void crearPedido_ConAdicionales_CalculaContadoresCorrectamente() {
        // Arrange — pedido con 2 unidades del producto y 1 adicional por unidad
        // la fórmula del servicio es: totalAdiciones += adicionesEnItem * cantidad
        // → 1 adicional × 2 unidades = 2
        Long clienteId = 1L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));

        Adicional adicionalMock = new Adicional();
        adicionalMock.setId(10L);
        adicionalMock.setNombre("Queso Extra");
        adicionalMock.setPrecio(2000.0);
        when(adicionalRepository.findById(10L)).thenReturn(Optional.of(adicionalMock));

        // Capturamos el pedido que llega a save() para inspeccionar sus valores
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApiDtos.SeleccionAdicionalRequest seleccion = new ApiDtos.SeleccionAdicionalRequest(10L, 2000.0);
        ApiDtos.ItemPedidoRequest itemRequest = new ApiDtos.ItemPedidoRequest(1L, 2, List.of(seleccion));
        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(List.of(itemRequest));

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        // Assert
        assertTrue(result.success());
        assertEquals(2, result.pedido().getCantidadProductos());  // 2 unidades del producto
        assertEquals(2, result.pedido().getCantidadAdiciones());  // 1 adicional × 2 unidades

        verify(adicionalRepository).findById(10L);
    }

    @Test
    void crearPedido_PrecioUnitarioCapturaElPrecioDelProducto() {
        // Arrange
        productoMock.setPrecio(15000.0);

        Long clienteId = 1L;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApiDtos.ItemPedidoRequest itemRequest = new ApiDtos.ItemPedidoRequest(1L, 1, new ArrayList<>());
        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(List.of(itemRequest));

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        // Assert — el ItemPedido debe guardar el precio que tenía el producto en ese momento
        assertTrue(result.success());
        assertNotNull(result.pedido().getItems());
        assertEquals(1, result.pedido().getItems().size());
        assertEquals(15000.0, result.pedido().getItems().get(0).getPrecioUnitario());
    }

    @Test
    void crearPedido_FechaCreacionEsAsignada() {
        // Arrange
        Long clienteId = 1L;
        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(clienteMock));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApiDtos.ItemPedidoRequest itemRequest = new ApiDtos.ItemPedidoRequest(1L, 1, new ArrayList<>());
        ApiDtos.CrearPedidoRequest request = new ApiDtos.CrearPedidoRequest(List.of(itemRequest));

        // Act
        PedidoService.CrearPedidoResult result = pedidoService.crearPedido(clienteId, request);

        // Assert — la fecha de creación debe estar entre "antes" y "ahora"
        assertTrue(result.success());
        assertNotNull(result.pedido().getFechaCreacion());
        assertFalse(result.pedido().getFechaCreacion().isBefore(antes));
        assertFalse(result.pedido().getFechaCreacion().isAfter(LocalDateTime.now().plusSeconds(1)));
    }

    // =========================================================================
    // cambiarEstado — casos adicionales
    // =========================================================================

    @Test
    void cambiarEstado_PedidoNoEncontrado_Falla() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(99L, "LISTO");

        // Assert
        assertFalse(result.success());
        assertEquals("Pedido no encontrado.", result.errorMessage());
        assertNull(result.pedido());

        verify(pedidoRepository).findById(99L);
        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cambiarEstado_EstadoInvalido_Falla() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstado("PENDIENTE");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));

        // Act
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(1L, "INVENTADO");

        // Assert
        assertFalse(result.success());
        assertTrue(result.errorMessage().contains("Estado no válido"));
        assertNull(result.pedido());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void cambiarEstado_EntregadoNormalizaACompletado() {
        // Arrange — "ENTREGADO" es un alias que el servicio convierte a "COMPLETADO"
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstado("EN_CAMINO");

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(1L, "ENTREGADO");

        // Assert
        assertTrue(result.success());
        assertEquals("COMPLETADO", result.pedido().getEstado());
    }

    @Test
    void cambiarEstado_COMPLETADO_AsignaFechaEntregaYLiberaDomiciliario() {
        // Arrange
        Domiciliario domiciliario = new Domiciliario();
        domiciliario.setId(1L);
        domiciliario.setDisponible(false); // estaba ocupado

        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstado("EN_CAMINO");
        pedidoExistente.setDomiciliario(domiciliario);

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(1L, "COMPLETADO");

        // Assert
        assertTrue(result.success());
        assertEquals("COMPLETADO", result.pedido().getEstado());

        // La fecha de entrega debe haberse asignado
        assertNotNull(result.pedido().getFechaEntrega());
        assertFalse(result.pedido().getFechaEntrega().isBefore(antes));

        // El domiciliario debe haberse liberado (disponible = true)
        assertTrue(domiciliario.getDisponible());
        verify(domiciliarioRepository).save(domiciliario);
    }

    @Test
    void cambiarEstado_CANCELADO_LiberaDomiciliario() {
        // Arrange
        Domiciliario domiciliario = new Domiciliario();
        domiciliario.setId(1L);
        domiciliario.setDisponible(false);

        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstado("EN_CAMINO");
        pedidoExistente.setDomiciliario(domiciliario);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PedidoService.CambiarEstadoResult result = pedidoService.cambiarEstado(1L, "CANCELADO");

        // Assert
        assertTrue(result.success());
        assertEquals("CANCELADO", result.pedido().getEstado());

        // El domiciliario debe haberse liberado
        assertTrue(domiciliario.getDisponible());
        verify(domiciliarioRepository).save(domiciliario);
    }

    // =========================================================================
    // asignarDomiciliario
    // =========================================================================

    @Test
    void asignarDomiciliario_Exitoso() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);
        pedidoExistente.setEstado("LISTO");

        Domiciliario domiciliario = new Domiciliario();
        domiciliario.setId(2L);
        domiciliario.setDisponible(true);
        domiciliario.setActivo(true);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(domiciliarioRepository.findById(2L)).thenReturn(Optional.of(domiciliario));
        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PedidoService.AsignarDomiciliarioResult result = pedidoService.asignarDomiciliario(1L, 2L);

        // Assert
        assertTrue(result.success());
        assertNotNull(result.pedido().getDomiciliario());
        assertEquals(2L, result.pedido().getDomiciliario().getId());
        assertNull(result.errorMessage());

        verify(pedidoRepository).save(pedidoExistente);
    }

    @Test
    void asignarDomiciliario_PedidoNoEncontrado_Falla() {
        // Arrange
        when(pedidoRepository.findById(99L)).thenReturn(Optional.empty());
        when(domiciliarioRepository.findById(1L)).thenReturn(Optional.of(new Domiciliario()));

        // Act
        PedidoService.AsignarDomiciliarioResult result = pedidoService.asignarDomiciliario(99L, 1L);

        // Assert
        assertFalse(result.success());
        assertEquals("Pedido no encontrado.", result.errorMessage());
        assertNull(result.pedido());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void asignarDomiciliario_DomiciliarioNoEncontrado_Falla() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(domiciliarioRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        PedidoService.AsignarDomiciliarioResult result = pedidoService.asignarDomiciliario(1L, 99L);

        // Assert
        assertFalse(result.success());
        assertEquals("Domiciliario no encontrado.", result.errorMessage());
        assertNull(result.pedido());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void asignarDomiciliario_DomiciliarioNoDisponible_Falla() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);

        Domiciliario domiciliario = new Domiciliario();
        domiciliario.setId(2L);
        domiciliario.setDisponible(false); // ocupado
        domiciliario.setActivo(true);

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(domiciliarioRepository.findById(2L)).thenReturn(Optional.of(domiciliario));

        // Act
        PedidoService.AsignarDomiciliarioResult result = pedidoService.asignarDomiciliario(1L, 2L);

        // Assert
        assertFalse(result.success());
        assertEquals("El domiciliario no está disponible.", result.errorMessage());
        assertNull(result.pedido());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }

    @Test
    void asignarDomiciliario_DomiciliarioNoActivo_Falla() {
        // Arrange
        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setId(1L);

        Domiciliario domiciliario = new Domiciliario();
        domiciliario.setId(2L);
        domiciliario.setDisponible(true);
        domiciliario.setActivo(false); // inactivo

        when(pedidoRepository.findById(1L)).thenReturn(Optional.of(pedidoExistente));
        when(domiciliarioRepository.findById(2L)).thenReturn(Optional.of(domiciliario));

        // Act
        PedidoService.AsignarDomiciliarioResult result = pedidoService.asignarDomiciliario(1L, 2L);

        // Assert
        assertFalse(result.success());
        assertEquals("El domiciliario no está activo.", result.errorMessage());
        assertNull(result.pedido());

        verify(pedidoRepository, never()).save(any(Pedido.class));
    }
}
