# Laboratorio API - Productos

API REST de productos desarrollada en **Spring Boot 3.5** (Java 17) para el laboratorio 05:
**"Desarrollo de aplicaciones Web: Validación y Manejo de Errores"**.

Incluye la **Actividad** solicitada:

- Nuevo campo `categoria` en la entidad `Producto` y en el `ProductoDTO`.
- Endpoint para **buscar productos por nombre**: `GET /api/productos/buscar?nombre=...` (búsqueda parcial, sin distinguir mayúsculas).

## Requisitos

- **JDK 17 o superior** (Spring Boot 3.5 requiere Java 17+).
- **MySQL** (solo si se ejecuta con el perfil por defecto).
- No es necesario instalar Maven: el proyecto incluye el **Maven Wrapper** (`mvnw` / `mvnw.cmd`).

## Clonar y ejecutar

```bash
git clone <URL-del-repositorio>
cd Lab05Web
```

### Opción 1: Con MySQL (uso normal del laboratorio)

```sql
CREATE DATABASE laboratorio_api;
```

```bash
mvnw.cmd spring-boot:run
```

Configuración por defecto: `jdbc:mysql://localhost:3306/laboratorio_api`, usuario `root`, sin contraseña.
Si tu entorno difiere, edita `src/main/resources/application.properties`.

### Opción 2: Modo demo rápido con H2 (sin MySQL)

Útil para probar la API sin instalar MySQL:

```bash
mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=test
```

### Ejecutar los tests (no requieren MySQL)

```bash
mvnw.cmd test
```

> El perfil `test` usa una base de datos **H2 en memoria**; los 8 tests de integración
> cubren CRUD, validación (`@Valid` + `GlobalExceptionHandler`) y búsqueda por nombre.

## Endpoints

| Método | URL                                            | Descripción                                  |
| ------ | ---------------------------------------------- | -------------------------------------------- |
| GET    | `/api/productos`                               | Lista todos los productos                    |
| GET    | `/api/productos/buscar?nombre=Monitor`         | Busca por nombre (parcial, sin distinguir mayúsculas) |
| GET    | `/api/productos/{id}`                          | Obtiene un producto por id (404 si no existe)|
| POST   | `/api/productos`                               | Crea un producto (validado) · 201            |
| PUT    | `/api/productos/{id}`                          | Actualiza un producto (validado)             |
| DELETE | `/api/productos/{id}`                          | Elimina un producto                          |

### Ejemplos con Postman (curl)

```bash
# POST (crear) -> 201
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Monitor","precio":50,"stock":20,"categoria":"Tecnologia"}'

# POST inválido -> 400 (respuesta del GlobalExceptionHandler)
curl -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" \
  -d '{"nombre":"","precio":0,"stock":-1,"categoria":""}'
# -> {"categoria":"La categoria es obligatoria","precio":"El precio debe ser mayor a 0","stock":"El stock no puede ser negativo","nombre":"El nombre es obligatorio"}

# GET buscar por nombre
curl "http://localhost:8080/api/productos/buscar?nombre=mon"

# PUT (actualizar)
curl -X PUT http://localhost:8080/api/productos/1 \
  -H "Content-Type: application/json" \
  -d '{"nombre":"Monitor actualizado","precio":50,"stock":20,"categoria":"Perifericos"}'

# DELETE
curl -X DELETE http://localhost:8080/api/productos/1
```

## Estructura del proyecto

```
src/main/java/com/tecsup/
├── LaboratorioApiApplication.java   # clase principal
├── model/Producto.java              # entidad JPA (nombre, precio, stock, categoria)
├── dto/ProductoDTO.java             # DTO con validaciones (@NotBlank, @Positive, @Min)
├── repository/ProductoRepository.java
├── service/ProductoService.java
├── controller/ProductoController.java
└── exception/GlobalExceptionHandler.java  # @RestControllerAdvice
```

## Preguntas y entregables

Las 5 respuestas, observaciones y conclusiones están en **[RESPUESTAS.md](RESPUESTAS.md)**.