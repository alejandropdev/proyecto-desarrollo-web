package com.muk.config;

import com.muk.entities.Adicional;
import com.muk.entities.Administrador;
import com.muk.entities.Carrito;
import com.muk.entities.Categoria;
import com.muk.entities.Cliente;
import com.muk.entities.Domiciliario;
import com.muk.entities.ItemCarrito;
import com.muk.entities.Operador;
import com.muk.entities.Pedido;
import com.muk.entities.Producto;
import com.muk.entities.SeleccionAdicional;
import com.muk.repository.AdicionalRepository;
import com.muk.repository.AdministradorRepository;
import com.muk.repository.CarritoRepository;
import com.muk.repository.CategoriaRepository;
import com.muk.repository.ClienteRepository;
import com.muk.repository.DomiciliarioRepository;
import com.muk.repository.ItemCarritoRepository;
import com.muk.repository.OperadorRepository;
import com.muk.repository.PedidoRepository;
import com.muk.repository.ProductoRepository;
import com.muk.repository.SeleccionAdicionalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Inicializa la base de datos H2 con datos de prueba al arrancar la aplicación.
 * Carga categorías, productos (con relación a categoría) y clientes.
 * Reemplaza la carga desde data.sql para seguir la arquitectura de la aplicación.
 */
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final AdicionalRepository adicionalRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final AdministradorRepository administradorRepository;
    private final OperadorRepository operadorRepository;
    private final DomiciliarioRepository domiciliarioRepository;
    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    private final SeleccionAdicionalRepository seleccionAdicionalRepository;
    private final PedidoRepository pedidoRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            cargarCategorias();
        }
        if (adicionalRepository.count() == 0) {
            cargarAdiciones();
        }
        if (productoRepository.count() == 0) {
            cargarProductos();
        }
        if (clienteRepository.count() == 0) {
            cargarClientes();
        }
        if (administradorRepository.count() == 0) {
            cargarAdministradores();
        }
        if (operadorRepository.count() == 0) {
            cargarOperadores();
        }
        if (domiciliarioRepository.count() == 0) {
            cargarDomiciliarios();
        }
        if (carritoRepository.count() == 0) {
            cargarCarritos();
        }
        if (itemCarritoRepository.count() == 0) {
            cargarItemsCarrito();
        }
        if (seleccionAdicionalRepository.count() == 0) {
            cargarSeleccionesAdicionales();
        }
        if (pedidoRepository.count() == 0) {
            cargarPedidos();
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

    private void cargarAdiciones() {
        Map<String, Categoria> categoriasPorNombre = categoriaRepository.findAll().stream()
                .collect(Collectors.toMap(Categoria::getNombre, c -> c));

        Categoria bbq = categoriasPorNombre.get("BBQ");
        Categoria burgers = categoriasPorNombre.get("BURGERS");
        Categoria chicken = categoriasPorNombre.get("CHICKEN");
        Categoria desserts = categoriasPorNombre.get("DESSERTS");
        Categoria drinks = categoriasPorNombre.get("DRINKS");

        List<Adicional> adiciones = List.of(
            new Adicional(null, "Salsa BBQ extra", bbq),
            new Adicional(null, "Papas fritas", bbq),
            new Adicional(null, "Ensalada", bbq),
            new Adicional(null, "Pan de ajo", bbq),
            new Adicional(null, "Coleslaw", bbq),
            new Adicional(null, "Papas fritas", burgers),
            new Adicional(null, "Pepinillos", burgers),
            new Adicional(null, "Queso extra", burgers),
            new Adicional(null, "Bacon", burgers),
            new Adicional(null, "Huevo", burgers),
            new Adicional(null, "Papas fritas", chicken),
            new Adicional(null, "Salsa ranch", chicken),
            new Adicional(null, "Aros de cebolla", chicken),
            new Adicional(null, "Ensalada", chicken),
            new Adicional(null, "Dip de queso", chicken),
            new Adicional(null, "Helado extra", desserts),
            new Adicional(null, "Crema batida", desserts),
            new Adicional(null, "Salsa de chocolate", desserts),
            new Adicional(null, "Frutos rojos", desserts),
            new Adicional(null, "Nuez", desserts),
            new Adicional(null, "Hielo extra", drinks),
            new Adicional(null, "Limón", drinks),
            new Adicional(null, "Crema", drinks),
            new Adicional(null, "Doble shot", drinks),
            new Adicional(null, "Vaso grande", drinks)
        );

        adicionalRepository.saveAll(adiciones);
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
    private void cargarAdministradores() {
        List<Administrador> admins = List.of(
                new Administrador(null, "admin", "1234"),
                new Administrador(null, "mukadmin", "admin123"),
                new Administrador(null, "adminops", "ops1234"),
                new Administrador(null, "adminventas", "ventas123"),
                new Administrador(null, "adminqa", "qa12345")
        );
        administradorRepository.saveAll(admins);
    }

    private void cargarOperadores() {
        List<Operador> operadores = List.of(
                new Operador(null, "Julian Herrera", "operador1", "hash-op-001"),
                new Operador(null, "Paula Medina", "operador2", "hash-op-002"),
                new Operador(null, "Santiago Rios", "operador3", "hash-op-003"),
                new Operador(null, "Daniela Cruz", "operador4", "hash-op-004"),
                new Operador(null, "Nicolas Vega", "operador5", "hash-op-005")
        );
        operadorRepository.saveAll(operadores);
    }

    private void cargarDomiciliarios() {
        List<Domiciliario> domiciliarios = List.of(
                new Domiciliario(null, "Oscar Mejia", "3205551001", "101000001"),
                new Domiciliario(null, "Felipe Leon", "3205551002", "101000002"),
                new Domiciliario(null, "Camilo Parra", "3205551003", "101000003"),
                new Domiciliario(null, "Luis Pardo", "3205551004", "101000004"),
                new Domiciliario(null, "Kevin Mora", "3205551005", "101000005")
        );
        domiciliarioRepository.saveAll(domiciliarios);
    }

    private void cargarCarritos() {
        List<Cliente> clientes = clienteRepository.findAll();
        if (clientes.size() < 5) {
            return;
        }

        List<Carrito> carritos = List.of(
                new Carrito(null, clientes.get(0)),
                new Carrito(null, clientes.get(1)),
                new Carrito(null, clientes.get(2)),
                new Carrito(null, clientes.get(3)),
                new Carrito(null, clientes.get(4))
        );
        carritoRepository.saveAll(carritos);
    }

    private void cargarItemsCarrito() {
        List<Carrito> carritos = carritoRepository.findAll();
        List<Producto> productos = productoRepository.findAll();
        if (carritos.size() < 5 || productos.size() < 10) {
            return;
        }

        List<ItemCarrito> items = List.of(
                new ItemCarrito(null, carritos.get(0), productos.get(0), 1, productos.get(0).getPrecio()),
                new ItemCarrito(null, carritos.get(0), productos.get(1), 2, productos.get(1).getPrecio()),
                new ItemCarrito(null, carritos.get(1), productos.get(2), 1, productos.get(2).getPrecio()),
                new ItemCarrito(null, carritos.get(1), productos.get(3), 1, productos.get(3).getPrecio()),
                new ItemCarrito(null, carritos.get(2), productos.get(4), 3, productos.get(4).getPrecio()),
                new ItemCarrito(null, carritos.get(2), productos.get(5), 1, productos.get(5).getPrecio()),
                new ItemCarrito(null, carritos.get(3), productos.get(6), 2, productos.get(6).getPrecio()),
                new ItemCarrito(null, carritos.get(3), productos.get(7), 1, productos.get(7).getPrecio()),
                new ItemCarrito(null, carritos.get(4), productos.get(8), 1, productos.get(8).getPrecio()),
                new ItemCarrito(null, carritos.get(4), productos.get(9), 2, productos.get(9).getPrecio())
        );
        itemCarritoRepository.saveAll(items);
    }

    private void cargarSeleccionesAdicionales() {
        List<ItemCarrito> items = itemCarritoRepository.findAll();
        List<Adicional> adicionales = adicionalRepository.findAll();
        if (items.size() < 5 || adicionales.size() < 10) {
            return;
        }

        List<SeleccionAdicional> selecciones = List.of(
                new SeleccionAdicional(null, items.get(0), adicionales.get(0), adicionales.get(0).getPrecio()),
                new SeleccionAdicional(null, items.get(0), adicionales.get(2), adicionales.get(2).getPrecio()),
                new SeleccionAdicional(null, items.get(1), adicionales.get(5), adicionales.get(5).getPrecio()),
                new SeleccionAdicional(null, items.get(2), adicionales.get(6), adicionales.get(6).getPrecio()),
                new SeleccionAdicional(null, items.get(3), adicionales.get(10), adicionales.get(10).getPrecio()),
                new SeleccionAdicional(null, items.get(4), adicionales.get(11), adicionales.get(11).getPrecio()),
                new SeleccionAdicional(null, items.get(5), adicionales.get(15), adicionales.get(15).getPrecio()),
                new SeleccionAdicional(null, items.get(6), adicionales.get(16), adicionales.get(16).getPrecio()),
                new SeleccionAdicional(null, items.get(7), adicionales.get(20), adicionales.get(20).getPrecio()),
                new SeleccionAdicional(null, items.get(8), adicionales.get(24), adicionales.get(24).getPrecio())
        );
        seleccionAdicionalRepository.saveAll(selecciones);
    }

    private void cargarPedidos() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<Operador> operadores = operadorRepository.findAll();
        List<Domiciliario> domiciliarios = domiciliarioRepository.findAll();
        if (clientes.size() < 5 || operadores.size() < 5 || domiciliarios.size() < 5) {
            return;
        }

        Pedido p1 = new Pedido(null, clientes.get(0), "PENDIENTE", LocalDateTime.now().minusHours(2));
        p1.setOperador(operadores.get(0));
        p1.setDomiciliario(domiciliarios.get(0));

        Pedido p2 = new Pedido(null, clientes.get(1), "EN_PREPARACION", LocalDateTime.now().minusHours(1));
        p2.setOperador(operadores.get(1));
        p2.setDomiciliario(domiciliarios.get(1));

        Pedido p3 = new Pedido(null, clientes.get(2), "EN_CAMINO", LocalDateTime.now().minusMinutes(40));
        p3.setOperador(operadores.get(2));
        p3.setDomiciliario(domiciliarios.get(2));

        Pedido p4 = new Pedido(null, clientes.get(3), "ENTREGADO", LocalDateTime.now().minusDays(1));
        p4.setOperador(operadores.get(3));
        p4.setDomiciliario(domiciliarios.get(3));
        p4.setFechaEntrega(LocalDateTime.now().minusDays(1).plusMinutes(45));

        Pedido p5 = new Pedido(null, clientes.get(4), "CANCELADO", LocalDateTime.now().minusHours(5));
        p5.setOperador(operadores.get(4));
        p5.setDomiciliario(domiciliarios.get(4));

        pedidoRepository.saveAll(List.of(p1, p2, p3, p4, p5));
    }

}
