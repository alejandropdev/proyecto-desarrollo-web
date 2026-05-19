package com.muk.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muk.controller.api.ApiDtos;
import com.muk.controller.api.ProductosApiController;
import com.muk.entities.Adicional;
import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import com.muk.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductosApiController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ProductosApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoriaMock;
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        categoriaMock = new Categoria();
        categoriaMock.setId(1L);
        categoriaMock.setNombre("Hamburguesas");

        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Hamburguesa Clásica");
        productoMock.setDescripcion("Deliciosa hamburguesa con carne de res");
        productoMock.setPrecio(18000.0);
        productoMock.setImagenUrl("https://example.com/hamburguesa.jpg");
        productoMock.setActivo(true);
        productoMock.setCategoria(categoriaMock);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/productos  –  Lista todos los productos (sin filtros)
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void GET_productos_sinFiltros_retorna200ConLista() throws Exception {
        Producto producto2 = new Producto();
        producto2.setId(2L);
        producto2.setNombre("Pizza Margarita");
        producto2.setDescripcion("Pizza con tomate y queso");
        producto2.setPrecio(22000.0);
        producto2.setImagenUrl("https://example.com/pizza.jpg");
        producto2.setActivo(true);
        producto2.setCategoria(categoriaMock);

        when(productoService.findByFilters(null, null))
                .thenReturn(List.of(productoMock, producto2));

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Hamburguesa Clásica"))
                .andExpect(jsonPath("$[1].nombre").value("Pizza Margarita"));

        verify(productoService).findByFilters(null, null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/productos?category=Hamburguesas  –  Lista con filtro de categoría
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void GET_productos_conFiltroCategoria_retorna200ConListaFiltrada() throws Exception {
        when(productoService.findByFilters("Hamburguesas", null))
                .thenReturn(List.of(productoMock));

        mockMvc.perform(get("/api/productos")
                        .param("category", "Hamburguesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Hamburguesa Clásica"))
                .andExpect(jsonPath("$[0].precio").value(18000.0));

        verify(productoService).findByFilters("Hamburguesas", null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/productos/{id}  –  Producto encontrado → 200
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void GET_productoById_existente_retorna200ConProducto() throws Exception {
        when(productoService.findById(1L)).thenReturn(Optional.of(productoMock));

        mockMvc.perform(get("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Hamburguesa Clásica"))
                .andExpect(jsonPath("$.precio").value(18000.0))
                .andExpect(jsonPath("$.activo").value(true));

        verify(productoService).findById(1L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/productos/{id}  –  Producto NO encontrado → 404
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void GET_productoById_inexistente_retorna404() throws Exception {
        when(productoService.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/productos/99"))
                .andExpect(status().isNotFound());

        verify(productoService).findById(99L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/productos  –  Creación exitosa → 201
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void POST_crearProducto_exitoso_retorna201ConProductoCreado() throws Exception {
        ApiDtos.ProductoUpsertRequest request = new ApiDtos.ProductoUpsertRequest(
                "Hamburguesa Clásica",
                "Deliciosa hamburguesa con carne de res",
                18000.0,
                "https://example.com/hamburguesa.jpg",
                1L,
                null
        );

        when(productoService.createProducto(any(ApiDtos.ProductoUpsertRequest.class)))
                .thenReturn(productoMock);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Hamburguesa Clásica"))
                .andExpect(jsonPath("$.precio").value(18000.0));

        verify(productoService).createProducto(any(ApiDtos.ProductoUpsertRequest.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // POST /api/productos  –  Datos inválidos (IllegalArgumentException) → 400
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void POST_crearProducto_datosInvalidos_retorna400() throws Exception {
        ApiDtos.ProductoUpsertRequest requestInvalida = new ApiDtos.ProductoUpsertRequest(
                null, null, -5.0, null, null, null
        );

        when(productoService.createProducto(any(ApiDtos.ProductoUpsertRequest.class)))
                .thenThrow(new IllegalArgumentException("Datos del producto inválidos"));

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestInvalida)))
                .andExpect(status().isBadRequest());

        verify(productoService).createProducto(any(ApiDtos.ProductoUpsertRequest.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/productos/{id}  –  Actualización exitosa → 200
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void PUT_actualizarProducto_existente_retorna200ConProductoActualizado() throws Exception {
        Producto productoActualizado = new Producto();
        productoActualizado.setId(1L);
        productoActualizado.setNombre("Hamburguesa Especial");
        productoActualizado.setDescripcion("Hamburguesa con queso doble");
        productoActualizado.setPrecio(22000.0);
        productoActualizado.setImagenUrl("https://example.com/hamburguesa-especial.jpg");
        productoActualizado.setActivo(true);
        productoActualizado.setCategoria(categoriaMock);

        ApiDtos.ProductoUpsertRequest request = new ApiDtos.ProductoUpsertRequest(
                "Hamburguesa Especial",
                "Hamburguesa con queso doble",
                22000.0,
                "https://example.com/hamburguesa-especial.jpg",
                1L,
                null
        );

        when(productoService.updateProducto(eq(1L), any(ApiDtos.ProductoUpsertRequest.class)))
                .thenReturn(Optional.of(productoActualizado));

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Hamburguesa Especial"))
                .andExpect(jsonPath("$.precio").value(22000.0));

        verify(productoService).updateProducto(eq(1L), any(ApiDtos.ProductoUpsertRequest.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PUT /api/productos/{id}  –  Producto no encontrado → 404
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void PUT_actualizarProducto_inexistente_retorna404() throws Exception {
        ApiDtos.ProductoUpsertRequest request = new ApiDtos.ProductoUpsertRequest(
                "Nombre", "Descripcion", 10000.0, "url", 1L, null
        );

        when(productoService.updateProducto(eq(99L), any(ApiDtos.ProductoUpsertRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/productos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(productoService).updateProducto(eq(99L), any(ApiDtos.ProductoUpsertRequest.class));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DELETE /api/productos/{id}  –  Eliminación exitosa → 200
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void DELETE_eliminarProducto_retorna200ConMensaje() throws Exception {
        doNothing().when(productoService).delete(1L);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto eliminado."));

        verify(productoService).delete(1L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/productos/{id}/adiciones-permitidas  –  Retorna lista de adiciones
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void GET_adicionesPermitidas_retorna200ConListaAdiciones() throws Exception {
        Adicional adicionalMock = new Adicional();
        adicionalMock.setId(1L);
        adicionalMock.setNombre("Queso Extra");
        adicionalMock.setPrecio(2000.0);
        adicionalMock.setActivo(true);
        adicionalMock.setCategoria(categoriaMock);

        when(productoService.obtenerAdicionalesPermitidos(1L))
                .thenReturn(List.of(adicionalMock));

        mockMvc.perform(get("/api/productos/1/adiciones-permitidas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Queso Extra"))
                .andExpect(jsonPath("$[0].precio").value(2000.0));

        verify(productoService).obtenerAdicionalesPermitidos(1L);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GET /api/productos  –  Lista vacía cuando no hay productos → 200 con []
    // ─────────────────────────────────────────────────────────────────────────
    @Test
    void GET_productos_listaVacia_retorna200ConArrayVacio() throws Exception {
        when(productoService.findByFilters(null, null)).thenReturn(List.of());

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productoService).findByFilters(null, null);
    }
}
