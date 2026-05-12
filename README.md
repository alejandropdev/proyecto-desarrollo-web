## MUK Website - Angular + Spring Boot REST

Aplicación del restaurante Muk migrada a arquitectura **SPA + API**:

- Frontend: **Angular** (`muk-angular`)
- Backend: **Spring Boot REST API** (`/api/**`)
- Persistencia: **Spring Data JPA + H2** (desarrollo)

## Requisitos

- Java 17+
- Maven 3.8+
- Node.js 18+ y npm

## Ejecutar el backend (Spring Boot)

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

Backend disponible en:

```text
http://localhost:8080
```

## Ejecutar el frontend (Angular)

En otra terminal:

```bash
cd muk-angular
npm install
npm start
```

Frontend disponible en:

```text
http://localhost:4200
```

El frontend consume la API vía rutas relativas (`/api/...`) y `npm start` ya levanta Angular con proxy hacia `http://localhost:8080` usando `proxy.conf.json`.

## Build de verificación

Backend:

```bash
mvn -DskipTests compile
```

Frontend:

```bash
cd muk-angular
npm run build
```

## Pruebas E2E (Selenium + Angular embebido)

Cada clase E2E arranca Spring Boot en un **puerto aleatorio**, sirve el build de Angular desde `classpath:/spa/` (generado en la fase Maven `prepare-package`) y ejecuta el flujo en **Chrome con ventana visible** (por defecto). Para CI sin interfaz gráfica usa `-De2e.headless=true`.

**Requisitos adicionales:** Node.js 18+ y npm (para el `frontend-maven-plugin`), y **Google Chrome** instalado (WebDriverManager descarga el ChromeDriver adecuado).

### Caso 1 — `AdminProductoMenuE2EIT`

Flujo de administrador: crea un producto con adiciones y verifica que aparece en el menú. Luego edita el producto añadiendo un tercer adicional y confirma la actualización en el menú.

```bash
cd muk-angular && npm run build:maven && cd .. && mvn failsafe:integration-test failsafe:verify -Dit.test=com.muk.e2e.AdminProductoMenuE2EIT
```

### Caso 2 — `ClientePedidoE2EIT`

Flujo completo de cliente + operador en dos pestañas del navegador:

1. Un usuario registrado inicia sesión y agrega al menos 2 comidas con 2 adicionales cada una.
2. Verifica que el carrito es correcto (productos, adicionales y total) antes de confirmar.
3. Confirma el pedido y espera actualizaciones de estado.
4. En otra pestaña, un operador inicia sesión, selecciona el pedido y lo avanza por todos los estados (`EN_PREPARACION → LISTO → EN_CAMINO → COMPLETADO`). Al pasar a `EN_CAMINO` se auto-asigna un domiciliario disponible.
5. Se verifica en la pestaña del usuario que el cambio de estado es visible.
6. Cuando el pedido queda completado, el usuario revisa su historial: se comprueba que aparecen todos los productos y adicionales seleccionados, y que el total del detalle coincide con el mostrado en el carrito (sin hardcodear el valor).

```bash
cd muk-angular && npm run build:maven && cd .. && mvn failsafe:integration-test failsafe:verify -Dit.test=com.muk.e2e.ClientePedidoE2EIT
```

**Credenciales de prueba usadas** (precargadas por `DataLoader`):

| Rol      | Usuario / Email   | Contraseña  |
|----------|-------------------|-------------|
| Cliente  | `sara@muk.com`    | `1234`      |
| Operador | `operador1`       | `hash-op-001` |

### Ejecutar todos los tests E2E

Sin tests unitarios de Surefire:

```bash
mvn verify -DskipUnitTests=true
```

Ciclo completo (tests unitarios + E2E):

```bash
mvn verify
```

Los tests unitarios (`mvn test`) **no** construyen el frontend; el build de Angular para la SPA embebida se ejecuta al empaquetar (`mvn package` o `mvn verify`).

## Estructura principal

```text
src/main/java/com/muk
├── MukApplication.java
├── config/
├── controller/api/       # Controladores REST por módulo
├── entities/
├── repository/
├── service/
└── service/impl/
```

```text
muk-angular/src/app
├── pages/
├── services/
├── models/
├── components/
└── app-routing.module.ts
```

## Notas

- El backend ya no sirve vistas Thymeleaf; la interfaz vive en Angular.
- Los endpoints REST se organizan por módulo (`menu`, `productos`, `clientes`, `operadores`, `categorias`, `admin`).
