## MUK Website - Backend Spring Boot

Aplicación web para el restaurante Muk.  
Se trata de un backend en **Spring Boot 3** que sirve plantillas **Thymeleaf** y recursos estáticos, y expone la lógica de negocio mediante controladores, servicios y repositorios con **Spring Data JPA** sobre base de datos **H2** en tiempo de desarrollo.

---

### Requisitos

- **Java 17**
- **Maven 3.6+**

Opcionalmente:

- Navegador moderno (para acceder a `http://localhost:8080`)

---

### Ejecución del proyecto

Desde la raíz del proyecto (`muk-website`):

```bash
mvn spring-boot:run
```

o bien, empaquetando y ejecutando el JAR:

```bash
mvn clean package
java -jar target/muk-website-backend-1.0.0.jar
```

Acceso en el navegador:

```text
http://localhost:8080
```

---

### Estructura general

Código Java:

```text
src/main/java/com/muk
├── MukApplication.java        # Punto de entrada Spring Boot
├── config/                    # Configuración y carga inicial de datos (DataLoader)
├── controller/                # Controladores MVC (páginas públicas y panel admin)
├── entities/                  # Entidades JPA (Cliente, Producto, Categoria, etc.)
├── repository/                # Repositorios Spring Data JPA
├── service/                   # Interfaces de servicios
└── service/impl/              # Implementaciones de servicios
```

Recursos:

```text
src/main/resources
├── templates/                 # Vistas Thymeleaf (públicas, clientes, admin, errores)
├── static/                    # CSS, JS y otros estáticos compartidos
└── application.properties     # Configuración de la aplicación (H2, logs, etc.)
```

---

### Flujo y componentes principales

- **Páginas públicas (`PagesController`)**
  - `/` muestra la landing principal con hero, concepto Muk, platos destacados y sección de ubicación.
  - `/menu` lista los productos con:
    - Búsqueda por nombre (`q`).
    - Filtro por categoría (`category`).
    - Carga condicional de adicionales asociados a la categoría seleccionada.
  - `/comida/{id}` muestra el detalle de un producto individual.
  - `/desafios`, `/ubicacion`, `/login`, `/registro` muestran las páginas de desafíos, ubicación, login y registro.

- **Clientes (`ClienteController`)**
  - CRUD básico de clientes en rutas bajo `/clientes`.
  - `/clientes/login` (GET/POST) inicia sesión buscando por email y contraseña.
  - `/clientes/registro` (GET/POST) registra nuevos clientes con validaciones de negocio.
  - `/clientes/perfil` muestra el perfil de cliente a partir del email recibido por query param.
  - `/clientes/perfil/editar` permite editar datos del perfil.
  - `/clientes/perfil/eliminar` elimina la cuenta del cliente.

- **Administración de platos (`AdminPlatoController`)**
  - `/admin/platos` lista los platos con búsqueda y filtro por categoría.
  - `/admin/platos/nuevo` formulario para crear un plato.
  - `/admin/platos/guardar` guarda o actualiza un plato.
  - `/admin/platos/{id}` muestra el detalle de un plato.
  - `/admin/platos/{id}/editar` formulario de edición.
  - `/admin/platos/{id}/eliminar` elimina el plato.

- **Autenticación administrador (`AdminAuthController`)**
  - `/admin/login` (GET) muestra formulario de login para administrador.
  - `/admin/login` (POST) valida credenciales contra `AdministradorRepository` y redirige al listado de platos de administración.

- **Capa de servicios**
  - Servicios como `ProductoService`, `ClienteService`, `CategoriaService`, `AdicionalService` encapsulan la lógica de negocio.
  - Las implementaciones (`ProductoServiceImpl`, etc.) orquestan validaciones, búsquedas y operaciones de escritura sobre los repositorios.

- **Persistencia (JPA/H2)**
  - Entidades como `Producto`, `Cliente`, `Categoria`, `Pedido`, `Carrito`, `ItemCarrito`, `Administrador`, `Operador`, `Domiciliario`, `Adicional` y `SeleccionAdicional` modelan las tablas.
  - Repositorios (`ProductoRepository`, `ClienteRepository`, `CategoriaRepository`, etc.) extienden `JpaRepository` para ofrecer operaciones CRUD y consultas derivadas.
  - Base de datos H2 se ejecuta en memoria o archivo según configuración en `application.properties`.

---

### Cómo funciona a alto nivel

- El usuario accede a rutas públicas que renderizan plantillas Thymeleaf con datos suministrados por la capa de servicios.
- La navegación de cliente final se centra en:
  - Descubrir el menú y sus categorías.
  - Explorar desafíos y ubicación del restaurante.
  - Registrarse, iniciar sesión y administrar su perfil (sin manejo de sesión de servidor, se apoya en parámetros como el email).
- El panel de administración permite a un administrador autenticado gestionar platos (crear, editar, eliminar) y visualizarlos con filtros y búsqueda.
- La capa de persistencia abstrae la base de datos; cambiar de H2 a otra base soportada por JPA implica principalmente ajustes de configuración.

---

### Dependencias principales (pom.xml)

- `spring-boot-starter-web`  
- `spring-boot-starter-thymeleaf`  
- `spring-boot-starter-data-jpa`  
- `com.h2database:h2` (runtime)  
- `spring-boot-devtools` (desarrollo)  
- `lombok` (reducción de boilerplate)  
- `spring-boot-starter-test` (pruebas)

---

### Entornos y configuración

- La configuración por defecto se encuentra en `src/main/resources/application.properties`.
- Incluye propiedades para:
  - Conexión y comportamiento de H2.
  - Opciones de logging.
  - Configuración propia de la aplicación Muk.

---

### Equipo

- Samuel José Velandia del Castillo
- Sara Muñoz
- Sebastián Vargas
- Alejandro Parrado