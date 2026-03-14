# MUK Website — Spring Boot Backend

This module runs the MUK restaurant site using **Spring Boot**.  
The application serves the static frontend (HTML, CSS, JS) and provides backend functionality through controllers, services, and repositories.

The backend now uses **Spring Data JPA** for database persistence and data management.

---

## Requirements

- Java 17
- Maven 3.6+

---

## Run the application

From the `muk` directory:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/muk-website-backend-1.0.0.jar
```

Then open the application in your browser:

```
http://localhost:8080
```

---

## Project Structure

```
src/main/java/com/muk
│
├── controller      → Handles HTTP requests
├── entities        → JPA entities (Cliente, Producto)
├── repository      → Data access layer using Spring Data JPA
├── service         → Business logic interfaces
├── service/impl    → Service implementations
└── web             → Application configuration and main class
```

Resources:

```
src/main/resources
├── static      → Static frontend files (HTML, CSS, JavaScript)
├── templates   → Thymeleaf templates
└── application.properties → Application configuration
```

---

## JPA Implementation

The application uses **Spring Data JPA** to manage data persistence.

### Entities

The following classes are mapped as database entities:

- `Producto`
- `Cliente`

Each entity includes the following JPA annotations:

- `@Entity`
- `@Table`
- `@Id`
- `@GeneratedValue`

Column constraints are defined using:

- `nullable`
- `unique`
- `length`

Example:

```java
@Column(nullable = false, unique = true, length = 100)
private String nombre;
```

---

## Repositories

Repositories extend `JpaRepository`, which provides built-in CRUD operations:

- `findAll()`
- `findById()`
- `save()`
- `deleteById()`

Custom query methods are also defined, such as:

- find products by category
- search products by name
- find clients by email
- authenticate clients using email and password

---

## Application Architecture

The project follows a layered architecture:

```
Controller → Service → Repository → Database
```

- **Controllers** handle HTTP requests.
- **Services** contain business logic.
- **Repositories** manage database access with JPA.
- **Entities** represent database tables.

---

## Features

- Product management
- Client registration
- Product search
- Category filtering
- Data persistence using JPA

---

## Authors

MUK Development Team