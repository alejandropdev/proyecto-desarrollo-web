package com.muk.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import com.muk.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductosApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductosApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    private Producto productoEjemplo;

    @BeforeEach
    void setUp() {
        Categoria categoria = new Categoria(1L, "Bebidas", "Bebidas frias y calientes");
        productoEjemplo = new Producto(1L, "Coca Cola", categoria, 2.50, "url", "Refresco");
        productoEjemplo.setActivo(true);
    }

    @Test
    void testObtenerProductos() throws Exception {
        Mockito.when(productoService.findByFilters(any(), any())).thenReturn(List.of(productoEjemplo));

        mockMvc.perform(get("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Coca Cola"))
                .andExpect(jsonPath("$[0].precio").value(2.50));
    }

    @Test
    void testObtenerProductoPorId_Exito() throws Exception {
        Mockito.when(productoService.findById(1L)).thenReturn(Optional.of(productoEjemplo));

        mockMvc.perform(get("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Coca Cola"));
    }

    @Test
    void testCrearProducto() throws Exception {
        ApiDtos.ProductoUpsertRequest request = new ApiDtos.ProductoUpsertRequest("Coca Cola", "Refresco", 2.50, "url", 1L, null);

        Mockito.when(productoService.createProducto(any(ApiDtos.ProductoUpsertRequest.class))).thenReturn(productoEjemplo);

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Coca Cola"));
    }

    @Test
    void testActualizarProducto_Exito() throws Exception {
        ApiDtos.ProductoUpsertRequest request = new ApiDtos.ProductoUpsertRequest("Coca Cola Editada", "Refresco Editado", 3.00, "url", 1L, null);
        
        Producto productoEditado = new Producto(1L, "Coca Cola Editada", productoEjemplo.getCategoria(), 3.00, "url", "Refresco Editado");

        Mockito.when(productoService.updateProducto(eq(1L), any(ApiDtos.ProductoUpsertRequest.class))).thenReturn(Optional.of(productoEditado));

        mockMvc.perform(put("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Coca Cola Editada"));
    }

    @Test
    void testEliminarProducto() throws Exception {
        Mockito.doNothing().when(productoService).delete(1L);

        mockMvc.perform(delete("/api/productos/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Producto eliminado."));
    }
}
