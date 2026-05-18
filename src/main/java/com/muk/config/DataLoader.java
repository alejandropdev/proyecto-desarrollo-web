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
import com.muk.entities.Role;
import com.muk.entities.SeleccionAdicional;
import com.muk.entities.UserEntity;
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
import com.muk.repository.RoleRepository;
import com.muk.repository.SeleccionAdicionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
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
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(
            CategoriaRepository categoriaRepository,
            AdicionalRepository adicionalRepository,
            ProductoRepository productoRepository,
            ClienteRepository clienteRepository,
            AdministradorRepository administradorRepository,
            OperadorRepository operadorRepository,
            DomiciliarioRepository domiciliarioRepository,
            CarritoRepository carritoRepository,
            ItemCarritoRepository itemCarritoRepository,
            SeleccionAdicionalRepository seleccionAdicionalRepository,
            PedidoRepository pedidoRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.categoriaRepository = categoriaRepository;
        this.adicionalRepository = adicionalRepository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.administradorRepository = administradorRepository;
        this.operadorRepository = operadorRepository;
        this.domiciliarioRepository = domiciliarioRepository;
        this.carritoRepository = carritoRepository;
        this.itemCarritoRepository = itemCarritoRepository;
        this.seleccionAdicionalRepository = seleccionAdicionalRepository;
        this.pedidoRepository = pedidoRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        cargarRoles();
        if (categoriaRepository.count() == 0) {
            cargarCategorias();
        }
        if (adicionalRepository.count() == 0) {
            cargarAdiciones();
        }
        if (productoRepository.count() == 0) {
            cargarProductos();
        }
        cargarAdicionesPermitidasProductos();
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

    private void cargarRoles() {
        if (roleRepository.findByName("ROLE_CLIENTE").isEmpty()) {
            roleRepository.save(Role.builder().name("ROLE_CLIENTE").build());
        }
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
        }
        if (roleRepository.findByName("ROLE_OPERADOR").isEmpty()) {
            roleRepository.save(Role.builder().name("ROLE_OPERADOR").build());
        }
    }

    private void cargarCategorias() {
        List<Categoria> categorias = List.of(
                new Categoria(null, "BBQ"),
                new Categoria(null, "BURGERS"),
                new Categoria(null, "CHICKEN"),
                new Categoria(null, "DESSERTS"),
                new Categoria(null, "DRINKS"));
        categoriaRepository.saveAll((Iterable<Categoria>) categorias);
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
                new Adicional(null, "Salsa BBQ extra", 2500.0, bbq),
                new Adicional(null, "Papas fritas", 6000.0, bbq),
                new Adicional(null, "Ensalada", 4500.0, bbq),
                new Adicional(null, "Pan de ajo", 3500.0, bbq),
                new Adicional(null, "Coleslaw", 4000.0, bbq),
                new Adicional(null, "Papas fritas", 6000.0, burgers),
                new Adicional(null, "Pepinillos", 1500.0, burgers),
                new Adicional(null, "Queso extra", 3500.0, burgers),
                new Adicional(null, "Bacon", 4500.0, burgers),
                new Adicional(null, "Huevo", 2500.0, burgers),
                new Adicional(null, "Papas fritas", 6000.0, chicken),
                new Adicional(null, "Salsa ranch", 2000.0, chicken),
                new Adicional(null, "Aros de cebolla", 5000.0, chicken),
                new Adicional(null, "Ensalada", 4500.0, chicken),
                new Adicional(null, "Dip de queso", 3000.0, chicken),
                new Adicional(null, "Helado extra", 4000.0, desserts),
                new Adicional(null, "Crema batida", 2500.0, desserts),
                new Adicional(null, "Salsa de chocolate", 2500.0, desserts),
                new Adicional(null, "Frutos rojos", 3500.0, desserts),
                new Adicional(null, "Nuez", 3000.0, desserts),
                new Adicional(null, "Hielo extra", 500.0, drinks),
                new Adicional(null, "Limón", 800.0, drinks),
                new Adicional(null, "Crema", 2000.0, drinks),
                new Adicional(null, "Doble shot", 4000.0, drinks),
                new Adicional(null, "Vaso grande", 1500.0, drinks));

        adicionalRepository.saveAll((Iterable<Adicional>) adiciones);
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
                new Producto(null, "Muk BBQ Ribs", bbq, 42000.0,
                        "https://images.unsplash.com/photo-1544025162-d76694265947",
                        "Costillas BBQ cocidas lentamente."),
                new Producto(null, "BBQ Smash Burger", burgers, 32000.0,
                        "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",
                        "Hamburguesa smash con salsa BBQ."),
                new Producto(null, "Muk Chicken Crunch", chicken, 28000.0,
                        "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58",
                        "Pollo crujiente con especias."),
                new Producto(null, "Chocolate Lava Muk", desserts, 17000.0,
                        "https://images.unsplash.com/photo-1578985545062-69928b1d9587", "Postre de chocolate fundido."),
                new Producto(null, "Muk Lemonade", drinks, 9000.0,
                        "https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd", "Limonada natural refrescante"),
                new Producto(null, "BBQ Mega Ribs", bbq, 45000.0,
                        "https://images.unsplash.com/photo-1558030006-450675393462", "Costillas BBQ tamaño gigante."),
                new Producto(null, "Double Muk Burger", burgers, 35000.0,
                        "https://images.unsplash.com/photo-1550547660-d9450f859349", "Doble carne y doble queso."),
                new Producto(null, "Chicken Fire Wings", chicken, 26000.0,
                        "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Alitas picantes estilo Muk."),
                new Producto(null, "Muk Brownie Tower", desserts, 18000.0,
                        "https://images.unsplash.com/photo-1606313564200-e75d5e30476c", "Brownie con helado."),
                new Producto(null, "Muk Mango Drink", drinks, 10000.0,
                        "https://images.unsplash.com/photo-1600271886742-f049cd451bba", "Bebida natural de mango"),
                new Producto(null, "BBQ Pulled Pork", bbq, 34000.0,
                        "https://images.unsplash.com/photo-1600891964599-f61ba0e24092", "Cerdo BBQ desmechado."),
                new Producto(null, "Muk Classic Burger", burgers, 27000.0,
                        "https://images.unsplash.com/photo-1550547660-d9450f859349", "Hamburguesa clásica Muk."),
                new Producto(null, "Chicken Gold Nuggets", chicken, 24000.0,
                        "https://images.unsplash.com/photo-1562967916-eb82221dfb36", "Nuggets crujientes."),
                new Producto(null, "Muk Cheesecake", desserts, 16000.0,
                        "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Cheesecake de la casa."),
                new Producto(null, "Muk Cola Drink", drinks, 8000.0,
                        "https://images.unsplash.com/photo-1581006852262-e4307cf6283a", "Refresco clásico"),
                new Producto(null, "BBQ Ribs Challenge", bbq, 48000.0,
                        "https://images.unsplash.com/photo-1544025162-d76694265947", "Reto Muk de costillas."),
                new Producto(null, "Triple Muk Burger", burgers, 38000.0,
                        "https://images.unsplash.com/photo-1550317138-10000687a72b", "Triple carne Muk."),
                new Producto(null, "Chicken Muk Sandwich", chicken, 26000.0,
                        "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Sandwich de pollo."),
                new Producto(null, "Muk Choco Bomb", desserts, 19000.0,
                        "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Explosión de chocolate."),
                new Producto(null, "Muk Orange Juice", drinks, 9000.0,
                        "https://images.unsplash.com/photo-1542444459-db63c3a0a3a3", "Jugo de naranja"),
                new Producto(null, "BBQ Beef Plate", bbq, 36000.0,
                        "https://images.unsplash.com/photo-1544025162-d76694265947", "Carne BBQ Muk."),
                new Producto(null, "Muk Bacon Burger", burgers, 33000.0,
                        "https://images.unsplash.com/photo-1550317138-10000687a72b", "Hamburguesa con bacon."),
                new Producto(null, "Chicken Muk Bucket", chicken, 39000.0,
                        "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58", "Bucket de pollo Muk."),
                new Producto(null, "Muk Strawberry Cake", desserts, 17000.0,
                        "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Pastel de fresa."),
                new Producto(null, "Muk Soda Drink", drinks, 7000.0,
                        "https://images.unsplash.com/photo-1581006852262-e4307cf6283a", "Soda refrescante"),
                new Producto(null, "BBQ Muk Plate XL", bbq, 52000.0,
                        "https://images.unsplash.com/photo-1544025162-d76694265947", "Plato BBQ extra grande."),
                new Producto(null, "Muk Double Cheese", burgers, 34000.0,
                        "https://images.unsplash.com/photo-1550547660-d9450f859349", "Doble queso Muk."),
                new Producto(null, "Chicken Muk Tenders", chicken, 25000.0,
                        "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Tenders crujientes."),
                new Producto(null, "Muk Ice Cream", desserts, 14000.0,
                        "https://images.unsplash.com/photo-1563805042-7684c019e1cb", "Helado Muk."),
                new Producto(null, "Muk Pineapple Drink", drinks, 9000.0,
                        "https://images.unsplash.com/photo-1600271886742-f049cd451bba", "Bebida de piña"),
                new Producto(null, "BBQ Muk Grill", bbq, 41000.0,
                        "https://images.unsplash.com/photo-1544025162-d76694265947", "Parrilla Muk."),
                new Producto(null, "Muk Monster Burger", burgers, 39000.0,
                        "https://images.unsplash.com/photo-1550317138-10000687a72b", "Hamburguesa monstruo Muk."),
                new Producto(null, "Chicken Muk Hot", chicken, 27000.0,
                        "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d", "Pollo picante Muk."),
                new Producto(null, "Muk Choco Cake", desserts, 16000.0,
                        "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Pastel chocolate Muk."),
                new Producto(null, "Muk Berry Drink", drinks, 10000.0,
                        "https://images.unsplash.com/photo-1600271886742-f049cd451bba", "Bebida de frutos rojos"),
                new Producto(null, "Muk BBQ Deluxe", bbq, 43000.0,
                        "https://images.unsplash.com/photo-1544025162-d76694265947", "Costillas BBQ estilo Muk."),
                new Producto(null, "Muk Ultimate Burger", burgers, 37000.0,
                        "https://images.unsplash.com/photo-1550547660-d9450f859349", "Hamburguesa premium Muk."),
                new Producto(null, "Muk Chicken Supreme", chicken, 29000.0,
                        "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d",
                        "Pollo crujiente especial Muk."),
                new Producto(null, "Muk Chocolate Dream", desserts, 18000.0,
                        "https://images.unsplash.com/photo-1551024601-bec78aea704b", "Postre de chocolate Muk."),
                new Producto(null, "Muk Tropical Drink", drinks, 10000.0,
                        "https://images.unsplash.com/photo-1600271886742-f049cd451bba",
                        "Bebida tropical refrescante."));

        productoRepository.saveAll((Iterable<Producto>) productos);
    }

    private void cargarAdicionesPermitidasProductos() {
        List<Producto> productos = productoRepository.findAll();
        if (productos.isEmpty()) {
            return;
        }

        Map<Long, List<Adicional>> adicionalesPorCategoriaId = adicionalRepository.findAll().stream()
                .filter(a -> a.getCategoria() != null && a.getCategoria().getId() != null)
                .collect(Collectors.groupingBy(a -> a.getCategoria().getId()));

        for (Producto producto : productos) {
            if (producto.getCategoria() == null || producto.getCategoria().getId() == null) {
                producto.setAdicionalesPermitidos(List.of());
                continue;
            }

            List<Adicional> permitidos = adicionalesPorCategoriaId.getOrDefault(producto.getCategoria().getId(),
                    List.of());
            producto.setAdicionalesPermitidos(permitidos);
        }

        productoRepository.saveAll(productos);
    }

    private void cargarClientes() {
        Role roleCliente = roleRepository.findByName("ROLE_CLIENTE").orElseThrow();

        Object[][] datos = {
            {"Sara", "Munoz", "sara@muk.com", "3001111111", "Bogota", "1234"},
            {"Juan", "Perez", "juan@muk.com", "3001111112", "Bogota", "1234"},
            {"Laura", "Gomez", "laura@muk.com", "3001111113", "Bogota", "1234"},
            {"Mateo", "Ruiz", "mateo@muk.com", "3001111114", "Bogota", "1234"},
            {"Ana", "Torres", "ana@muk.com", "3001111115", "Bogota", "1234"},
            {"Carlos", "Lopez", "carlos@muk.com", "3001111116", "Bogota", "1234"},
            {"Valentina", "Rojas", "valentina@muk.com", "3001111117", "Bogota", "1234"},
            {"Andres", "Castro", "andres@muk.com", "3001111118", "Bogota", "1234"},
            {"Camila", "Vargas", "camila@muk.com", "3001111119", "Bogota", "1234"},
            {"Daniel", "Moreno", "daniel@muk.com", "3001111120", "Bogota", "1234"}
        };

        for (Object[] d : datos) {
            String email = (String) d[2];
            String password = (String) d[5];
            UserEntity user = UserEntity.builder()
                    .username(email)
                    .password(passwordEncoder.encode(password))
                    .roles(new HashSet<>(Set.of(roleCliente)))
                    .build();
            Cliente cliente = new Cliente();
            cliente.setNombre((String) d[0]);
            cliente.setApellido((String) d[1]);
            cliente.setEmail(email);
            cliente.setTelefono((String) d[3]);
            cliente.setDireccion((String) d[4]);
            cliente.setUserEntity(user);
            clienteRepository.save(cliente);
        }
    }

    private void cargarAdministradores() {
        Role roleAdmin = roleRepository.findByName("ROLE_ADMIN").orElseThrow();

        Object[][] datos = {
            {"admin", "1234"},
            {"mukadmin", "admin123"},
            {"adminops", "ops1234"},
            {"adminventas", "ventas123"},
            {"adminqa", "qa12345"}
        };

        for (Object[] d : datos) {
            String usuario = (String) d[0];
            String password = (String) d[1];
            UserEntity user = UserEntity.builder()
                    .username(usuario)
                    .password(passwordEncoder.encode(password))
                    .roles(new HashSet<>(Set.of(roleAdmin)))
                    .build();
            Administrador admin = new Administrador();
            admin.setUsuario(usuario);
            admin.setUserEntity(user);
            administradorRepository.save(admin);
        }
    }

    private void cargarOperadores() {
        Role roleOperador = roleRepository.findByName("ROLE_OPERADOR").orElseThrow();

        Object[][] datos = {
            {"Julian Herrera", "operador1", "hash-op-001"},
            {"Paula Medina", "operador2", "hash-op-002"},
            {"Santiago Rios", "operador3", "hash-op-003"},
            {"Daniela Cruz", "operador4", "hash-op-004"},
            {"Nicolas Vega", "operador5", "hash-op-005"},
            {"Mariana Lopez", "operador6", "hash-op-006"},
            {"Esteban Ramirez", "operador7", "hash-op-007"},
            {"Valeria Cardenas", "operador8", "hash-op-008"},
            {"Sebastian Pineda", "operador9", "hash-op-009"},
            {"Gabriela Molina", "operador10", "hash-op-010"},
            {"Felipe Arias", "operador11", "hash-op-011"},
            {"Natalia Ospina", "operador12", "hash-op-012"},
            {"Juan David Suarez", "operador13", "hash-op-013"},
            {"Laura Milena Prieto", "operador14", "hash-op-014"},
            {"Andres Felipe Buitrago", "operador15", "hash-op-015"},
            {"Catalina Mendez", "operador16", "hash-op-016"},
            {"Kevin Alexander Forero", "operador17", "hash-op-017"},
            {"Alejandra Tovar", "operador18", "hash-op-018"},
            {"Miguel Angel Porras", "operador19", "hash-op-019"},
            {"Diana Marcela Benitez", "operador20", "hash-op-020"}
        };

        for (Object[] d : datos) {
            String usuario = (String) d[1];
            String password = (String) d[2];
            UserEntity user = UserEntity.builder()
                    .username(usuario)
                    .password(passwordEncoder.encode(password))
                    .roles(new HashSet<>(Set.of(roleOperador)))
                    .build();
            Operador operador = new Operador();
            operador.setNombre((String) d[0]);
            operador.setUsuario(usuario);
            operador.setActivo(true);
            operador.setUserEntity(user);
            operadorRepository.save(operador);
        }
    }

    private void cargarDomiciliarios() {
        List<Domiciliario> domiciliarios = List.of(
                new Domiciliario(null, "Oscar Mejia", "3205551001", "101000001"),
                new Domiciliario(null, "Felipe Leon", "3205551002", "101000002"),
                new Domiciliario(null, "Camilo Parra", "3205551003", "101000003"),
                new Domiciliario(null, "Luis Pardo", "3205551004", "101000004"),
                new Domiciliario(null, "Kevin Mora", "3205551005", "101000005"));
        domiciliarioRepository.saveAll((Iterable<Domiciliario>) domiciliarios);
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
                new Carrito(null, clientes.get(4)));
        carritoRepository.saveAll((Iterable<Carrito>) carritos);
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
                new ItemCarrito(null, carritos.get(4), productos.get(9), 2, productos.get(9).getPrecio()));
        itemCarritoRepository.saveAll((Iterable<ItemCarrito>) items);
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
                new SeleccionAdicional(null, items.get(8), adicionales.get(24), adicionales.get(24).getPrecio()));
        seleccionAdicionalRepository.saveAll((Iterable<SeleccionAdicional>) selecciones);
    }

    private void cargarPedidos() {
        List<Cliente> clientes = clienteRepository.findAll();
        List<Operador> operadores = operadorRepository.findAll();
        List<Domiciliario> domiciliarios = domiciliarioRepository.findAll();
        if (clientes.size() < 10 || operadores.size() < 20 || domiciliarios.size() < 5) {
            return;
        }

        Pedido p1 = new Pedido(null, clientes.get(0), "PENDIENTE", LocalDateTime.now().minusHours(2));
        p1.setOperador(operadores.get(0));

        Pedido p2 = new Pedido(null, clientes.get(1), "EN_PREPARACION", LocalDateTime.now().minusHours(1));
        p2.setOperador(operadores.get(1));

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

        Pedido p6 = new Pedido(null, clientes.get(5), "PENDIENTE", LocalDateTime.now().minusMinutes(25));
        p6.setOperador(operadores.get(5));

        Pedido p7 = new Pedido(null, clientes.get(6), "EN_PREPARACION", LocalDateTime.now().minusHours(3));
        p7.setOperador(operadores.get(6));

        Pedido p8 = new Pedido(null, clientes.get(7), "EN_CAMINO", LocalDateTime.now().minusMinutes(55));
        p8.setOperador(operadores.get(7));
        p8.setDomiciliario(domiciliarios.get(2));

        Pedido p9 = new Pedido(null, clientes.get(8), "ENTREGADO", LocalDateTime.now().minusDays(2));
        p9.setOperador(operadores.get(8));
        p9.setDomiciliario(domiciliarios.get(3));
        p9.setFechaEntrega(LocalDateTime.now().minusDays(2).plusMinutes(38));

        Pedido p10 = new Pedido(null, clientes.get(9), "CANCELADO", LocalDateTime.now().minusHours(7));
        p10.setOperador(operadores.get(9));
        p10.setDomiciliario(domiciliarios.get(4));

        Pedido p11 = new Pedido(null, clientes.get(0), "EN_PREPARACION", LocalDateTime.now().minusHours(4));
        p11.setOperador(operadores.get(10));

        Pedido p12 = new Pedido(null, clientes.get(1), "EN_CAMINO", LocalDateTime.now().minusMinutes(70));
        p12.setOperador(operadores.get(11));
        p12.setDomiciliario(domiciliarios.get(1));

        Pedido p13 = new Pedido(null, clientes.get(2), "ENTREGADO", LocalDateTime.now().minusDays(3));
        p13.setOperador(operadores.get(12));
        p13.setDomiciliario(domiciliarios.get(2));
        p13.setFechaEntrega(LocalDateTime.now().minusDays(3).plusMinutes(52));

        Pedido p14 = new Pedido(null, clientes.get(3), "PENDIENTE", LocalDateTime.now().minusMinutes(12));
        p14.setOperador(operadores.get(13));

        Pedido p15 = new Pedido(null, clientes.get(4), "CANCELADO", LocalDateTime.now().minusHours(9));
        p15.setOperador(operadores.get(14));
        p15.setDomiciliario(domiciliarios.get(4));

        Pedido p16 = new Pedido(null, clientes.get(5), "EN_CAMINO", LocalDateTime.now().minusMinutes(33));
        p16.setOperador(operadores.get(15));
        p16.setDomiciliario(domiciliarios.get(0));

        Pedido p17 = new Pedido(null, clientes.get(6), "ENTREGADO", LocalDateTime.now().minusDays(4));
        p17.setOperador(operadores.get(16));
        p17.setDomiciliario(domiciliarios.get(1));
        p17.setFechaEntrega(LocalDateTime.now().minusDays(4).plusMinutes(41));

        Pedido p18 = new Pedido(null, clientes.get(7), "PENDIENTE", LocalDateTime.now().minusMinutes(8));
        p18.setOperador(operadores.get(17));

        Pedido p19 = new Pedido(null, clientes.get(8), "EN_PREPARACION", LocalDateTime.now().minusHours(6));
        p19.setOperador(operadores.get(18));
        p19.setDomiciliario(domiciliarios.get(3));

        Pedido p20 = new Pedido(null, clientes.get(9), "ENTREGADO", LocalDateTime.now().minusDays(5));
        p20.setOperador(operadores.get(19));
        p20.setDomiciliario(domiciliarios.get(4));
        p20.setFechaEntrega(LocalDateTime.now().minusDays(5).plusMinutes(47));

        pedidoRepository.saveAll((Iterable<Pedido>) List.of(
                p1, p2, p3, p4, p5, p6, p7, p8, p9, p10,
                p11, p12, p13, p14, p15, p16, p17, p18, p19, p20));
    }
}
