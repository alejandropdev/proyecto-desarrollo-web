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
}
