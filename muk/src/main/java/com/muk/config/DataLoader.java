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
 * Carga 5 categorías, 40 comidas (muk-bang) y 10 clientes.
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
            new Categoria(null, "Hamburguesas Gigantes"),
            new Categoria(null, "Ramen XXL"),
            new Categoria(null, "Pollo y Frituras"),
            new Categoria(null, "Guarniciones y Sides"),
            new Categoria(null, "Postres Compartidos")
        );
        categoriaRepository.saveAll(categorias);
    }

    private void cargarProductos() {
        Map<String, Categoria> categoriasPorNombre = categoriaRepository.findAllByOrderByNombreAsc().stream()
                .collect(Collectors.toMap(Categoria::getNombre, c -> c));

        String placeholderImg = "/img/placeholder.jpg";
        Categoria hamburguesas = categoriasPorNombre.get("Hamburguesas Gigantes");
        Categoria ramen = categoriasPorNombre.get("Ramen XXL");
        Categoria pollo = categoriasPorNombre.get("Pollo y Frituras");
        Categoria guarniciones = categoriasPorNombre.get("Guarniciones y Sides");
        Categoria postres = categoriasPorNombre.get("Postres Compartidos");

        List<Producto> productos = List.of(
            new Producto(null, "Doble Mega Burger XXL", hamburguesas, 28900.0, placeholderImg, "Doble carne, doble queso, bacon y salsa especial. Porción muk-bang para compartir o reto personal."),
            new Producto(null, "Triple Cheese Monster", hamburguesas, 31900.0, placeholderImg, "Tres tipos de queso fundido, carne angus y pan brioche. El clásico que no puede faltar en tu muk-bang."),
            new Producto(null, "Burger Monte Everest", hamburguesas, 34900.0, placeholderImg, "Cuatro pisos de carne, queso cheddar, jalapeños y salsa BBQ. Para los más atrevidos."),
            new Producto(null, "Mega Bacon Explosion", hamburguesas, 29900.0, placeholderImg, "Carne premium envuelta en bacon crujiente, aros de cebolla y salsa ranch. Explosión de sabor."),
            new Producto(null, "Burger Doble Pollo Crispy", hamburguesas, 27900.0, placeholderImg, "Doble filete de pollo empanizado, lechuga, tomate y mayonesa de ajo. Crujiente por fuera, jugoso por dentro."),
            new Producto(null, "Mega Clásica con Huevo", hamburguesas, 26900.0, placeholderImg, "Carne a la parrilla, huevo frito, queso y tocino. Desayuno muk-bang estilo americano."),
            new Producto(null, "Burger BBQ Ribs", hamburguesas, 33900.0, placeholderImg, "Costillas desmenuzadas, carne angus y salsa BBQ casera. Fusión de grill en una sola hamburguesa."),
            new Producto(null, "Doble Western Burger", hamburguesas, 29900.0, placeholderImg, "Doble carne, aros de cebolla, pepinillos y salsa western. Sabor ranchero en porción XXL."),
            new Producto(null, "Ramen Gigante Miso", ramen, 24900.0, placeholderImg, "Bowl XXL de caldo miso, fideos, cerdo chashu, huevo y nori. Porción para muk-bang auténtico."),
            new Producto(null, "Ramen Tonkotsu XXL", ramen, 26900.0, placeholderImg, "Caldo de cerdo 12 horas, fideos frescos y toppings abundantes. Cremoso e intenso."),
            new Producto(null, "Ramen Picante Inferno", ramen, 25900.0, placeholderImg, "Caldo picante con chili oil, cerdo, maíz y cebollín. Para amantes del picante en muk-bang."),
            new Producto(null, "Ramen de Mariscos Gigante", ramen, 29900.0, placeholderImg, "Caldo de mariscos, camarones, mejillones y calamar. Mar en un bowl XXL."),
            new Producto(null, "Ramen Curry XXL", ramen, 25900.0, placeholderImg, "Fusión de ramen y curry japonés. Cremoso, especiado y adictivo en porción muk-bang."),
            new Producto(null, "Ramen Vegetariano Gigante", ramen, 22900.0, placeholderImg, "Caldo de setas y vegetales, tofu y huevo. Opción vegetariana en tamaño XXL."),
            new Producto(null, "Ramen Doble Carne", ramen, 28900.0, placeholderImg, "Doble porción de chashu, huevo marinado y menma. Para los que nunca tienen suficiente."),
            new Producto(null, "Ramen Kimchi XXL", ramen, 24900.0, placeholderImg, "Caldo con kimchi coreano, cerdo y fideos. Fusión coreano-japonesa en bowl gigante."),
            new Producto(null, "Pollo Frito XL (12 piezas)", pollo, 32900.0, placeholderImg, "Doce piezas de pollo crujiente estilo coreano. Porción muk-bang para compartir."),
            new Producto(null, "Alitas Picantes XXL (24 unidades)", pollo, 29900.0, placeholderImg, "Veinticuatro alitas en salsa picante dulce. Reto muk-bang de alitas."),
            new Producto(null, "Combo Pollo + Papas Gigante", pollo, 35900.0, placeholderImg, "Pollo crispy, papas fritas XL y salsa. El combo muk-bang por excelencia."),
            new Producto(null, "Tenders Gigantes (8 unidades)", pollo, 24900.0, placeholderImg, "Ocho tenders de pollo empanizado con miel mostaza. Crujientes y jugosos."),
            new Producto(null, "Pollo a la Barbacoa XXL", pollo, 31900.0, placeholderImg, "Medio pollo en salsa barbacoa, papas y coleslaw. Porción sureña estilo muk-bang."),
            new Producto(null, "Nuggets Gigantes (20 unidades)", pollo, 22900.0, placeholderImg, "Veinte nuggets de pollo con salsas a elección. Snack muk-bang por excelencia."),
            new Producto(null, "Pollo Agridulce XXL", pollo, 27900.0, placeholderImg, "Pollo crujiente con salsa agridulce y arroz. Clásico asiático en porción gigante."),
            new Producto(null, "Alitas Buffalo Mega (18 unidades)", pollo, 27900.0, placeholderImg, "Dieciocho alitas en salsa buffalo con apio y ranch. Picante y cremoso."),
            new Producto(null, "Papas Frituras Gigantes", guarniciones, 12900.0, placeholderImg, "Porción XL de papas crujientes con salsa. El side muk-bang imprescindible."),
            new Producto(null, "Aros de Cebolla XXL", guarniciones, 14900.0, placeholderImg, "Aros de cebolla empanizados en porción abundante. Crujientes por fuera, dulces por dentro."),
            new Producto(null, "Palitos de Queso (12 unidades)", guarniciones, 15900.0, placeholderImg, "Doce palitos de queso mozzarella fundido. Perfectos para muk-bang."),
            new Producto(null, "Elote Gigante con Especias", guarniciones, 11900.0, placeholderImg, "Mazorca XL con mayonesa, queso y chili. Side mexicano para tu muk-bang."),
            new Producto(null, "Tots de Papa XXL", guarniciones, 13900.0, placeholderImg, "Tater tots crujientes en porción generosa. Ideales para acompañar cualquier plato."),
            new Producto(null, "Coleslaw Familiar", guarniciones, 9900.0, placeholderImg, "Ensalada de repollo cremosa en porción para compartir. Refresca entre bocados."),
            new Producto(null, "Aros de Calabacín (10 unidades)", guarniciones, 13900.0, placeholderImg, "Aros de calabacín empanizados con salsa ranch. Opción más ligera pero igual de adictiva."),
            new Producto(null, "Mac and Cheese Gigante", guarniciones, 16900.0, placeholderImg, "Macarrones con queso cremoso en porción XXL. Comfort food para muk-bang."),
            new Producto(null, "Brownie Montaña con Helado", postres, 15900.0, placeholderImg, "Brownie caliente, helado de vainilla y salsa de chocolate. Postre muk-bang para compartir."),
            new Producto(null, "Sundae Familiar XXL", postres, 17900.0, placeholderImg, "Helado, crema batida, nueces y salsa. El sundae que no termina."),
            new Producto(null, "Cheesecake Gigante por Porción", postres, 14900.0, placeholderImg, "Porción generosa de cheesecake cremoso con frutos rojos. Postre clásico en tamaño muk-bang."),
            new Producto(null, "Donuts Gigantes (6 unidades)", postres, 13900.0, placeholderImg, "Seis donuts glaseados de distintos sabores. Para endulzar tu muk-bang."),
            new Producto(null, "Torta de Chocolate XXL", postres, 16900.0, placeholderImg, "Porción grande de torta de chocolate húmeda. Para los amantes del cacao."),
            new Producto(null, "Waffle Gigante con Frutas", postres, 15900.0, placeholderImg, "Waffle crujiente con helado, frutas y sirope. Desayuno o postre muk-bang."),
            new Producto(null, "Milkshake Doble (1 litro)", postres, 12900.0, placeholderImg, "Milkshake cremoso de vainilla o chocolate en tamaño compartido. Bebida postre."),
            new Producto(null, "Churros con Chocolate (12 unidades)", postres, 13900.0, placeholderImg, "Doce churros crujientes con salsa de chocolate. Postre español en porción muk-bang.")
        );

        productoRepository.saveAll(productos);
    }

    private void cargarClientes() {
        List<Cliente> clientes = List.of(
            new Cliente(null, "Carlos", "García López", "carlos.garcia@email.com", "3001112233", "Calle 45 #12-34, Bogotá", "clave123"),
            new Cliente(null, "María", "Rodríguez Pérez", "maria.rodriguez@email.com", "3102223344", "Carrera 20 #56-78, Medellín", "clave123"),
            new Cliente(null, "Andrés", "Martínez Sánchez", "andres.martinez@email.com", "3203334455", "Avenida 68 #90-12, Cali", "clave123"),
            new Cliente(null, "Laura", "Hernández Gómez", "laura.hernandez@email.com", "3154445566", "Calle 100 #15-20, Bogotá", "clave123"),
            new Cliente(null, "Diego", "López Fernández", "diego.lopez@email.com", "3185556677", "Carrera 43 #70-80, Medellín", "clave123"),
            new Cliente(null, "Sofía", "González Ruiz", "sofia.gonzalez@email.com", "3016667788", "Avenida 19 #120-30, Bogotá", "clave123"),
            new Cliente(null, "Juan", "Díaz Moreno", "juan.diaz@email.com", "3027778899", "Calle 50 #22-10, Barranquilla", "clave123"),
            new Cliente(null, "Valentina", "Torres Vega", "valentina.torres@email.com", "3118889900", "Carrera 15 #45-55, Cartagena", "clave123"),
            new Cliente(null, "Nicolás", "Ramírez Castro", "nicolas.ramirez@email.com", "3129990011", "Avenida 80 #10-25, Bogotá", "clave123"),
            new Cliente(null, "Isabella", "Flórez Ortiz", "isabella.florez@email.com", "3190001122", "Calle 72 #5-15, Bucaramanga", "clave123")
        );

        clienteRepository.saveAll(clientes);
    }
}
