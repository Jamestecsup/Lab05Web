# Respuestas - Laboratorio 05

**Curso:** Desarrollo de aplicaciones Web - 4 - C24 - Sección A - B
**Alumno:** Coello Palomino, Ricardo

## Actividad: respuestas

### 1. ¿Por qué usamos un DTO en lugar de exponer directamente la entidad?

Porque el DTO actúa como una capa de separación entre lo que recibe el cliente y lo que se guarda en la base de datos. Esto permite:

- **Validar y transformar** los datos de entrada sin acoplarlos al modelo de persistencia: en el DTO se aplican `@NotBlank`, `@Positive`, `@Min`, mientras que la entidad `Producto` solo representa la tabla.
- **No exponer** campos internos o sensibles (por ejemplo, el `id` generado o campos que el cliente no debe enviar).
- **Desacoplar**: si la tabla o el modelo cambian (por ejemplo, al agregar `categoria`), la API externa se mantiene estable.
- En el controlador, el flujo es: `DTO validado -> entidad -> repositorio`, y al responder se devuelve la entidad persistida. Esto también evita usar la entidad directamente en la capa web, que es una mala práctica.

### 2. ¿Qué función cumple @Valid?

`@Valid @RequestBody ProductoDTO dto` activa la **validación de Bean Validation** sobre el objeto JSON recibido antes de que se procese en el método del controlador. En concreto:

- Dispara las reglas declaradas en el DTO (`@NotBlank`, `@Positive`, `@Min`).
- Si alguna regla falla, se lanza `MethodArgumentNotValidException`.
- Esa excepción es capturada por el `GlobalExceptionHandler` (con `@ExceptionHandler`), que devuelve un JSON con los errores por campo y el código **400 Bad Request**.

Sin `@Valid`, las validaciones del DTO simplemente se ignorarían y se aceptarían datos inválidos.

### 3. ¿Qué pasaría si eliminamos @RestControllerAdvice?

`@RestControllerAdvice` registra un manejador global de excepciones para todos los controladores. Si lo eliminamos:

- `MethodArgumentNotValidException` ya **no sería capturada** por nuestro manejador.
- Spring devolvería la respuesta de error **por defecto** del framework (Whitelabel / `DefaultErrorAttributes`): un JSON o HTML genérico con código 500 o 400, **sin los mensajes personalizados** por campo ("El nombre es obligatorio", etc.).
- La API perdería consistencia en el formato de respuesta de errores y el mantenimiento sería más difícil, porque habría que repetir la lógica de manejo de errores en cada controlador.

### 4. ¿Qué hace @Autowired?

`@Autowired` realiza la **inyección de dependencias**: Spring localiza un bean gestionado por el contenedor (en este caso `ProductoService` y, dentro de él, `ProductoRepository`) y lo asigna automáticamente al atributo. De este modo **no creamos las dependencias con `new`**, sino que el contenedor de Spring las crea, las gestiona (ciclo de vida) y las inyecta. Esto facilita las pruebas (mocks) y el desacoplamiento entre capas.

### 5. ¿Cuál es la diferencia entre save(), findById() y findAll()?

Son métodos de `JpaRepository` (Spring Data JPA):

| Método | Qué hace | Retorna | Uso típico |
| ------ | -------- | ------- | ---------- |
| `save(entidad)` | Inserta o actualiza una entidad (si el id es `null`/0 hace INSERT; si el id existe hace UPDATE) | `Producto` (con id asignado) | Crear (`POST`) y actualizar (`PUT`) |
| `findById(id)` | Busca por la clave primaria | `Optional<Producto>` (vacío si no existe) | Obtener por id (`GET /{id}`); con `.orElse(null)` se verifica si existe |
| `findAll()` | Devuelve todos los registros | `List<Producto>` | Listar (`GET`) |

---

## Observaciones

1. Al agregar el campo `categoria` fue necesario actualizar en cadena la entidad, el DTO, el servicio, el controlador y el `application.properties`; es fácil olvidar una de las capas y provocar errores de compilación o campos vacíos.
2. El orden de los `@GetMapping("/buscar")` y `@GetMapping("/{id}")` importa: si el endpoint de búsqueda se definiera después de la ruta con `{id}`, Spring podría intentar convertir "buscar" a `Long` y devolver 400. Se resolvió declarando la ruta literal antes de la ruta con variable.
3. En mi equipo no estaba instalado Maven ni el JDK 17, así que instalé el JDK 17 de Temurin y usé el Maven Wrapper (`mvnw`) incluido en el proyecto para poder compilar y correr los tests sin configuraciones adicionales.
4. Para validar el código sin necesidad de tener MySQL levantado en todo momento, configuré un perfil de prueba (`test`) con base de datos H2 en memoria y tests de integración con MockMvc; esto permitió verificar el CRUD, las validaciones y la búsqueda de forma rápida.
5. El `GlobalExceptionHandler` solo captura `MethodArgumentNotValidException` (como pide el laboratorio); si en el futuro se lanzan excepciones de negocio propias, habría que agregar manejadores adicionales con `@ExceptionHandler`.

## Conclusiones

1. El uso de DTO + validación con Bean Validation permite mantener la API limpia y con respuestas de error consistentes, lo que mejora la experiencia del consumidor del servicio y reduce la corrupción de datos.
2. Comprender cómo funciona `@RestControllerAdvice` es clave: centraliza el manejo de errores y evita repetir lógica en cada controlador.
3. La separación en capas (Controller -> Service -> Repository) facilita el mantenimiento y las pruebas; los cambios como agregar `categoria` se resuelven de forma ordenada cuando se siguen las capas en orden.
4. Usar el Maven Wrapper y un perfil de prueba con H2 fue la solución más práctica para garantizar que el proyecto compila y funciona sin depender del entorno local (sin Maven o sin MySQL).
5. El laboratorio evidenció la importancia de probar los endpoints (Postman / tests de integración): la validación y el manejo de errores solo se perciben correctamente al verificar los códigos HTTP 400, 404 y 201 generados.