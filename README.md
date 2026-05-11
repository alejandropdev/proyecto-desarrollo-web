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

## Prueba E2E (Selenium + Angular embebido)

La clase `AdminProductoMenuE2EIT` arranca Spring Boot en un **puerto aleatorio**, sirve el build de Angular desde `classpath:/spa/` (generado en la fase Maven `prepare-package`) y ejecuta el flujo en **Chrome headless**.

**Requisitos adicionales:** Node.js 18+ y npm (para el `frontend-maven-plugin`), y **Google Chrome** instalado (WebDriverManager descarga el ChromeDriver adecuado).

Ejecutar solo la integración E2E (incluye `npm install` / `npm run build:maven` en `muk-angular`), sin tests unitarios de Surefire:

```bash
mvn verify -DskipUnitTests=true
```

O el ciclo completo (tests unitarios + E2E):

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
