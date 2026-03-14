package com.muk.config;

import com.muk.entities.Categoria;
import com.muk.entities.Cliente;
import com.muk.entities.Producto;
import com.muk.repository.CategoriaRepository;
import com.muk.repository.ClienteRepository;
import com.muk.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Inicializa la base de datos H2 con datos de prueba al arrancar la aplicación.
 * Carga categorías, productos (con relación a categoría) y clientes.
 * Reemplaza la carga desde data.sql para seguir la arquitectura de la aplicación.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;

    public DataLoader(CategoriaRepository categoriaRepository, ProductoRepository productoRepository, ClienteRepository clienteRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            cargarCategorias();
        }
        if (productoRepository.count() == 0) {
            cargarProductos();
        }
        if (clienteRepository.count() == 0) {
            cargarClientes();
        }
    }

    private void cargarCategorias() {
        List<Categoria> categorias = List.of(
            new Categoria(null, "BBQ"),
            new Categoria(null, "BURGERS"),
            new Categoria(null, "CHICKEN"),
            new Categoria(null, "DESSERTS"),
            new Categoria(null, "DRINKS")
        );
        categoriaRepository.saveAll(categorias);
    }

    private void cargarProductos() {
        Map<String, Categoria> categoriasPorNombre = categoriaRepository.findAll().stream()
                .collect(Collectors.toMap(Categoria::getNombre, c -> c));

        Categoria bbq = categoriasPorNombre.get("BBQ");
        Categoria burgers = categoriasPorNombre.get("BURGERS");
        Categoria chicken = categoriasPorNombre.get("CHICKEN");
        Categoria desserts = categoriasPorNombre.get("DESSERTS");
        Categoria drinks = categoriasPorNombre.get("DRINKS");

        List<Producto> productos = List.of(
            new Producto(null, "Muk BBQ Ribs", bbq, 42000.0, "https://images.unsplash.com/photo-1544025162-d76694265947", "Costillas BBQ cocidas lentamente."),
            new Producto(null, "BBQ Smash Burger", burgers, 32000.0, "https://images.unsplash.com/photo-1568901346375-23c9450c58cd", "Hamburguesa smash con salsa BBQ."),
            new Producto(null, "Muk Chicken Crunch", chicken, 28000.0, "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58", "Pollo crujiente con especias."),
            new Producto(null, "Chocolate Lava Muk", desserts, 17000.0, "https://images.unsplash.com/photo-1578985545062-69928b1d9587", "Postre de chocolate fundido."),
            new Producto(null, "Muk Lemonade", drinks, 9000.0, "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd", "Limonada natural refrescante"),
            new Producto(null, "BBQ Mega Ribs", bbq, 45000.0, "https://images.unsplash.com/photo-1558030006-450675393462", "Costillas BBQ tamaño gigante."),
            new Producto(null, "Double Muk Burger", burgers, 35000.0, "https://images.unsplash.com/photo-1550547660-d9450f859349", "Doble carne y doble queso."),
            new Producto(null, "Chicken Fire Wings", chicken, 26000.0, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Alitas picantes estilo Muk."),
            new Producto(null, "Muk Brownie Tower", desserts, 18000.0, "https://images.unsplash.com/photo-1606313564200-e75d5e30476c", "Brownie con helado."),
            new Producto(null, "Muk Mango Drink", drinks, 10000.0, "https://images.unsplash.com/photo-1600271886742-f049cd451bba", "Bebida natural de mango"),
            new Producto(null, "BBQ Pulled Pork", bbq, 34000.0, "https://images.unsplash.com/photo-1600891964599-f61ba0e24092", "Cerdo BBQ desmechado."),
            new Producto(null, "Muk Classic Burger", burgers, 27000.0, "https://images.unsplash.com/photo-1550547660-d9450f859349", "Hamburguesa clásica Muk."),
            new Producto(null, "Chicken Gold Nuggets", chicken, 24000.0, "https://images.unsplash.com/photo-1562967916-eb82221dfb36", "Nuggets crujientes."),
            new Producto(null, "Muk Cheesecake", desserts, 16000.0, "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Cheesecake de la casa."),
            new Producto(null, "Muk Cola Drink", drinks, 8000.0, "https://images.unsplash.com/photo-1581006852262-e4307cf6283a", "Refresco clásico"),
            new Producto(null, "BBQ Ribs Challenge", bbq, 48000.0, "https://images.unsplash.com/photo-1544025162-d76694265947", "Reto Muk de costillas."),
            new Producto(null, "Triple Muk Burger", burgers, 38000.0, "https://images.unsplash.com/photo-1550317138-10000687a72b", "Triple carne Muk."),
            new Producto(null, "Chicken Muk Sandwich", chicken, 26000.0, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Sandwich de pollo."),
            new Producto(null, "Muk Choco Bomb", desserts, 19000.0, "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Explosión de chocolate."),
            new Producto(null, "Muk Orange Juice", drinks, 9000.0, "https://images.unsplash.com/photo-1542444459-db63c3a0a3a3", "Jugo de naranja"),
            new Producto(null, "BBQ Beef Plate", bbq, 36000.0, "https://images.unsplash.com/photo-1544025162-d76694265947", "Carne BBQ Muk."),
            new Producto(null, "Muk Bacon Burger", burgers, 33000.0, "https://images.unsplash.com/photo-1550317138-10000687a72b", "Hamburguesa con bacon."),
            new Producto(null, "Chicken Muk Bucket", chicken, 39000.0, "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58", "Bucket de pollo Muk."),
            new Producto(null, "Muk Strawberry Cake", desserts, 17000.0, "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Pastel de fresa."),
            new Producto(null, "Muk Soda Drink", drinks, 7000.0, "https://images.unsplash.com/photo-1581006852262-e4307cf6283a", "Soda refrescante"),
            new Producto(null, "BBQ Muk Plate XL", bbq, 52000.0, "https://images.unsplash.com/photo-1544025162-d76694265947", "Plato BBQ extra grande."),
            new Producto(null, "Muk Double Cheese", burgers, 34000.0, "https://images.unsplash.com/photo-1550547660-d9450f859349", "Doble queso Muk."),
            new Producto(null, "Chicken Muk Tenders", chicken, 25000.0, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Tenders crujientes."),
            new Producto(null, "Muk Ice Cream", desserts, 14000.0, "https://images.unsplash.com/photo-1563805042-7684c019e1cb", "Helado Muk."),
            new Producto(null, "Muk Pineapple Drink", drinks, 9000.0, "https://images.unsplash.com/photo-1600271886742-f049cd451bba", "Bebida de piña"),
            new Producto(null, "BBQ Muk Grill", bbq, 41000.0, "https://images.unsplash.com/photo-1544025162-d76694265947", "Parrilla Muk."),
            new Producto(null, "Muk Monster Burger", burgers, 39000.0, "https://images.unsplash.com/photo-1550317138-10000687a72b", "Hamburguesa monstruo Muk."),
            new Producto(null, "Chicken Muk Hot", chicken, 27000.0, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Pollo picante Muk."),
            new Producto(null, "Muk Choco Cake", desserts, 16000.0, "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Pastel chocolate Muk."),
            new Producto(null, "Muk Berry Drink", drinks, 10000.0, "https://images.unsplash.com/photo-1600271886742-f049cd451bba", "Bebida de frutos rojos"),
            new Producto(null, "Muk BBQ Deluxe", bbq, 43000.0, "https://images.unsplash.com/photo-1544025162-d76694265947", "Costillas BBQ estilo Muk."),
            new Producto(null, "Muk Ultimate Burger", burgers, 37000.0, "https://images.unsplash.com/photo-1550547660-d9450f859349", "Hamburguesa premium Muk."),
            new Producto(null, "Muk Chicken Supreme", chicken, 29000.0, "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Pollo crujiente especial Muk."),
            new Producto(null, "Muk Chocolate Dream", desserts, 18000.0, "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Postre de chocolate Muk."),
            new Producto(null, "Muk Tropical Drink", drinks, 10000.0, "https://images.unsplash.com/photo-1600271886742-f049cd451bba", "Bebida tropical refrescante.")
        );

        productoRepository.saveAll(productos);
    }

    private void cargarClientes() {
        List<Cliente> clientes = List.of(
            new Cliente(null, "Sara", "Munoz", "sara@muk.com", "3001111111", "Bogota", "1234"),
            new Cliente(null, "Juan", "Perez", "juan@muk.com", "3001111112", "Bogota", "1234"),
            new Cliente(null, "Laura", "Gomez", "laura@muk.com", "3001111113", "Bogota", "1234"),
            new Cliente(null, "Mateo", "Ruiz", "mateo@muk.com", "3001111114", "Bogota", "1234"),
            new Cliente(null, "Ana", "Torres", "ana@muk.com", "3001111115", "Bogota", "1234"),
            new Cliente(null, "Carlos", "Lopez", "carlos@muk.com", "3001111116", "Bogota", "1234"),
            new Cliente(null, "Valentina", "Rojas", "valentina@muk.com", "3001111117", "Bogota", "1234"),
            new Cliente(null, "Andres", "Castro", "andres@muk.com", "3001111118", "Bogota", "1234"),
            new Cliente(null, "Camila", "Vargas", "camila@muk.com", "3001111119", "Bogota", "1234"),
            new Cliente(null, "Daniel", "Moreno", "daniel@muk.com", "3001111120", "Bogota", "1234")
        );

        clienteRepository.saveAll(clientes);
    }
}
