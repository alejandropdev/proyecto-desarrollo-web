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
