package com.muk.repository;

import com.muk.entities.Categoria;
import com.muk.entities.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductoRepositoryConsultasTest {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    private Producto p1;
    private Producto p2;
    private Producto p3;

    @BeforeEach
    void setUp() {
        productoRepository.deleteAll();
        categoriaRepository.deleteAll();

        Categoria cat1 = new Categoria(null, "Bebidas", "Bebidas frias y calientes");
        Categoria cat2 = new Categoria(null, "Postres", "Dulces");
        categoriaRepository.save(cat1);
        categoriaRepository.save(cat2);

        p1 = new Producto(null, "Coca Cola", cat1, 2.50, "url1", "Refresco");
        p1.setActivo(true);
        p2 = new Producto(null, "Torta de Chocolate", cat2, 5.00, "url2", "Pastel");
        p2.setActivo(true);
        p3 = new Producto(null, "Agua Mineral", cat1, 1.50, "url3", "Agua sin gas");
        p3.setActivo(false);

        productoRepository.save(p1);
        productoRepository.save(p2);
        productoRepository.save(p3);
    }

    @Test
    void testBuscarPorNombreParcial() {
        List<Producto> resultados = productoRepository.findByNombreContainingIgnoreCase("coca");
        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Coca Cola");
    }

    @Test
    void testBuscarPorNombreParcial_NoEncontrado() {
        List<Producto> resultados = productoRepository.findByNombreContainingIgnoreCase("pepsi");
        assertThat(resultados).isEmpty();
    }

    @Test
    void testBuscarPorPrecioMenorOIgual() {
        List<Producto> resultados = productoRepository.findByPrecioLessThanEqual(2.50);
        assertThat(resultados).hasSize(2);
        assertThat(resultados).extracting(Producto::getNombre).containsExactlyInAnyOrder("Coca Cola", "Agua Mineral");
    }

    @Test
    void testBuscarPorPrecioMenorOIgual_NoEncontrado() {
        List<Producto> resultados = productoRepository.findByPrecioLessThanEqual(1.00);
        assertThat(resultados).isEmpty();
    }

    @Test
    void testBuscarPorCategoria() {
        List<Producto> resultados = productoRepository.findByCategoria_NombreIgnoreCase("bebidas");
        assertThat(resultados).hasSize(2);
        assertThat(resultados).extracting(Producto::getNombre).containsExactlyInAnyOrder("Coca Cola", "Agua Mineral");
    }

    @Test
    void testBuscarPorCategoria_NoEncontrado() {
        List<Producto> resultados = productoRepository.findByCategoria_NombreIgnoreCase("carnes");
        assertThat(resultados).isEmpty();
    }

    @Test
    void testBuscarProductosActivos() {
        List<Producto> resultados = productoRepository.findByActivoTrue();
        assertThat(resultados).hasSize(2);
        assertThat(resultados).extracting(Producto::getNombre).containsExactlyInAnyOrder("Coca Cola", "Torta de Chocolate");
    }

    @Test
    void testBuscarProductosActivos_NoEncuentraInactivos() {
        List<Producto> resultados = productoRepository.findByActivoTrue();
        assertThat(resultados).extracting(Producto::getNombre).doesNotContain("Agua Mineral");
    }

    @Test
    void testOrdenarProductosPorPrecioAscendente() {
        List<Producto> resultados = productoRepository.findAllByOrderByPrecioAsc();
        assertThat(resultados).hasSize(3);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Agua Mineral");
        assertThat(resultados.get(1).getNombre()).isEqualTo("Coca Cola");
        assertThat(resultados.get(2).getNombre()).isEqualTo("Torta de Chocolate");
    }
}
