# MUK Website — Spring Boot Backend

This module runs the MUK restaurant site on Spring Boot. The same static frontend (HTML, CSS, JS) is served from the classpath, and a REST API is available for menu data.

## Requirements

- **Java 17**
- **Maven 3.6+**

## Run the application

From the `backend` directory:

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/muk-website-backend-1.0.0.jar
```

Then open:

- **Site:** http://localhost:8080/ (e.g. http://localhost:8080/index.html, http://localhost:8080/menu.html)
- **API:** http://localhost:8080/api/foods (list all), http://localhost:8080/api/foods/FOOD-001 (by id), `?category=Burgers`, `?q=burger`

## Project structure

- `src/main/java/com/muk/` — Spring Boot app and food API (controller, service, repository, model)
- `src/main/resources/static/` — Static site (copied from project root): HTML, CSS, JS, `src/` (bootstrap, menu, comida, etc.)

The frontend currently uses the in-memory `fakeDb` in JS; the same data is exposed at `/api/foods` if you later switch the UI to fetch from the API.
