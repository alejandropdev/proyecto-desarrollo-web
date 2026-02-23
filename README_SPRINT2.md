# Sprint 2 — Fake DB de comidas (MVC por capas)

## Qué se implementó

- Entidad **Food** (modelo) con: `id`, `name`, `description`, `price`, `imageUrl`, `category`, `available`.
- **Fake DB** en memoria usando `Map` con **seed inicial de 10 comidas** e IDs únicos (`FOOD-001`…`FOOD-010`).
- Arquitectura por capas:
  - **Repository**: consultas al `Map` (sin lógica de UI).
  - **Service**: validación simple y métodos de negocio.
  - **Controller**: fachada para el uso desde `main.js`.
- Demo por consola en `src/main.js` (sin UI).

## Capas (Repository + Service + Controller)

- **`FoodRepository`**: accede a la fuente de datos (fake DB) y expone métodos de consulta.
- **`FoodService`**: valida parámetros (por ejemplo `id`/`category`) y delega al repository.
- **`FoodController`**: orquesta llamadas al service para casos de uso (listar y detalle).

## Estructura de carpetas

```
/index.html
/styles.css
/src/main.js
/src/models/Food.js
/src/data/fakeDb.js
/src/repositories/FoodRepository.js
/src/services/FoodService.js
/src/controllers/FoodController.js
/README_SPRINT2.md
```

## Cómo ejecutarlo

Necesitas servir el proyecto con un servidor HTTP (por ES Modules).

### Opción A: Live Server (VSCode/Cursor)

- Abrir `index.html`
- Click derecho → **Open with Live Server**
- Abrir consola del navegador (DevTools) para ver los `console.log`

### Opción B: Python

Desde la raíz del proyecto:

```bash
python -m http.server 5500
```

Luego abrir `http://localhost:5500` y mirar la consola del navegador.

## Ejemplo de salida esperada en consola

La salida exacta puede variar, pero el flujo es:

1) Lista completa (array de foods):

```js
[
  { id: "FOOD-001", name: "The Beast Burger", ... },
  { id: "FOOD-002", name: "Nuclear Ramen", ... },
  // ...
]
```

2) Detalle por id real (objeto o `null` si no existe):

```js
{ id: "FOOD-001", name: "The Beast Burger", ... }
```

3) Filtrado por categoría:

```js
[
  { id: "FOOD-001", category: "Burgers", ... },
  { id: "FOOD-008", category: "Burgers", ... }
]
```

4) Búsqueda por nombre:

```js
[
  { id: "FOOD-001", name: "The Beast Burger", ... },
  { id: "FOOD-008", name: "Truffle Mushroom Burger", ... }
]
```

