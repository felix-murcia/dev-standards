```markdown
# ☕ Spring Boot + BBDD - Architecture Guide (Java 17/21+)

> 📌 Extends: `standards/ARCHITECTURE.md` (Hexagonal + SOLID Universal)  
> 🔄 Stack: Java 17/21+ | Spring Boot 3.x | Spring Data JPA | CompletableFuture | Records | Var/Pattern Matching  
> 📦 Indicador: `pom.xml` o `build.gradle`

---

## 📁 Estructura Obligatoria
```
src/main/java/com/felixmurcia/app/
├── AppApplication.java
├── domain/
│   ├── entities/
│   ├── valueobjects/          # records inmutables con validación
│   ├── events/
│   ├── exceptions/
│   └── ports/
├── application/
│   ├── usecases/              # CompletableFuture atómicos
│   ├── dtos/                  # records puros (solo datos)
│   └── services/
├── presentation/
│   ├── controllers/
│   ├── schemas/               # records de request/response
│   ├── mappers/
│   └── exceptions/            # @RestControllerAdvice + switch expressions
└── infrastructure/
    ├── persistence/
    ├── database/
    ├── external/
    └── config/
```

---

## 🔑 Implementación por Capa (Java Moderno)

### Domain (ValueObjects & Entities como Records)
```java
// domain/valueobjects/Email.java
public record Email(String value) {
    public Email {
        if (!value.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        value = value.toLowerCase().trim();
    }
}

// domain/entities/User.java
public record User(UserId id, Email email, String passwordHash, String name, LocalDateTime createdAt) {
    public static User create(Email email, String passwordHash, String name) {
        return new User(UserId.generate(), email, passwordHash, name, LocalDateTime.now());
    }
}
```

### Application (Use Case con CompletableFuture Atomizado)
```java
// application/usecases/CreateUserUseCase.java
@Service
@RequiredArgsConstructor
public class CreateUserUseCase {
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationPort notificationPort;

    public CompletableFuture<UserResponse> executeAsync(CreateUserRequest request) {
        var checkExists = CompletableFuture.supplyAsync(() -> {
            if (userRepository.existsByEmail(request.email())) throw new UserAlreadyExistsException(request.email());
            return true;
        });

        var createUser = CompletableFuture.supplyAsync(() ->
            User.create(request.email(), passwordEncoder.encode(request.password()), request.name())
        );

        return checkExists
            .thenCombine(createUser, (_, user) -> user)
            .thenApplyAsync(userRepository::save)
            .thenApplyAsync(saved -> {
                notificationPort.sendWelcomeEmail(saved.email());
                return UserResponse.fromEntity(saved);
            })
            .exceptionally(ex -> {
                log.error("Async flow failed: {}", ex.getCause().getMessage());
                throw new CompletionException(ex);
            });
    }
}
```

### Presentation (Controllers con Records & Switch Exhaustivo)
```java
// presentation/schemas/UserApiRequest.java
public record UserApiRequest(
    @NotBlank @Email String email,
    @NotBlank String name,
    @NotBlank @Size(min = 8) String password
) {}

// presentation/controllers/UserController.java
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final CreateUserUseCase useCase;

    @PostMapping
    public CompletableFuture<ResponseEntity<UserApiResponse>> create(@Valid @RequestBody UserApiRequest req) {
        return useCase.executeAsync(new CreateUserRequest(req.email(), req.name(), req.password()))
            .thenApply(ResponseEntity::ok)
            .exceptionally(this::mapError);
    }

    private ResponseEntity<UserApiResponse> mapError(Throwable ex) {
        return switch (ex.getCause()) {
            case UserAlreadyExistsException e -> ResponseEntity.status(HttpStatus.CONFLICT).body(new UserApiResponse("CONFLICT", e.email().value()));
            case IllegalArgumentException e -> ResponseEntity.badRequest().body(new UserApiResponse("BAD_REQUEST", e.getMessage()));
            default -> ResponseEntity.internalServerError().body(new UserApiResponse("INTERNAL_ERROR", "Unexpected failure"));
        };
    }
}
```

---

## 💧 Base de Datos (Outbound Adapter Moderno)

```java
// infrastructure/persistence/repositories/UserRepositoryImpl.java
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryPort {
    private final JpaUserRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public User save(User user) {
        var entity = mapper.toJpa(user);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public List<User> findAllActive() {
        return jpaRepository.findAll().stream()
            .filter(e -> e.status() == UserStatus.ACTIVE)
            .map(mapper::toDomain)
            .toList();
    }
}
```

---

## 📐 SOLID + Modern Java Practices

| Principio | Implementación con Java Moderno |
|-----------|--------------------------------|
| **SRP** | `record` para datos, `class` para comportamiento. Métodos < 20 líneas con `var` y lambdas. |
| **OCP** | `switch` expressions + `sealed interfaces` para extensibilidad segura sin `instanceof` chains. |
| **LSP** | Pattern matching `instanceof` para subtipos. No lanzar `UnsupportedOperationException` en implementaciones. |
| **ISP** | Interfaces funcionales (`@FunctionalInterface`) para estrategias y callbacks. |
| **DIP** | Inyección por constructor + `var` para resolución local. Spring 6 mejora inference en `@Bean`. |

### 🛠️ Facilidades Obligatorias (Java 17/21+)
- `record` para DTOs, ValueObjects, API payloads y respuestas.
- `var` para variables locales, streams, y builders complejos (tipado inferido, no débil; Java sigue siendo estáticamente tipado).
- `switch` expressions + `yield` para lógica condicional compleja.
- Pattern matching: `if (obj instanceof User u && u.isActive())`
- `sealed classes/interfaces` para dominios cerrados y exhaustivos.
- `CompletableFuture` con composición atómica (`supplyAsync`, `thenApply`, `thenCombine`, `handle`, `exceptionally`).
- `Optional` chaining: `.map()`, `.flatMap()`, `.orElseThrow()`.
- Text blocks (`"""`) para queries nativas, JSON, o plantillas SQL.
- Collections: `.toList()`, `.toSet()`, `.toUnmodifiableList()` (evita `Collectors`).

---

## 🚨 Violaciones a Rechazar (Agente)

- Clases DTO con getters/setters/Lombok cuando un `record` es suficiente.
- Métodos síncronos bloqueantes para I/O externo o composición de servicios.
- Uso de `Collectors.toList()` en Java 16+ (usar `.toList()` directo).
- `if/else` anidados extensos (usar `switch` expressions o pattern matching).
- Tipado explícito redundante (`List<String> list = new ArrayList<>()` → `var list = new ArrayList<String>()`).
- `CompletableFuture.get()` o `.join()` en hilos de request (bloquea el dispatcher).
- Inmutabilidad rota en `record`s (no modificar campos en compact constructor para lógica de negocio compleja).
- `@Transactional` aplicado a métodos que retornan `CompletableFuture` sin manejo explícito de transacción por hilo.

---

## ✅ Checklist de Validación

- [ ] DTOs y ValueObjects son `record` inmutables con validación en compact constructor.
- [ ] Flujos async usan `CompletableFuture` con composición atómica (prohibido `.get()`).
- [ ] Variables locales usan `var` cuando el tipo es inferible claramente.
- [ ] Lógica condicional usa `switch` expressions o pattern matching.
- [ ] Streams terminan con `.toList()`, `.toSet()`, o `.toUnmodifiableList()`.
- [ ] No hay `try/catch` en cadenas de `CompletableFuture` (usar `.exceptionally()` o `.handle()`).
- [ ] Transacciones async gestionadas con `@TransactionalEventListener` o programación manual por etapa.
- [ ] Código sin boilerplate Lombok innecesario (`@Data`, `@Builder` en records).
- [ ] Interfaces de dominio son `sealed` si el conjunto de implementaciones es cerrado.

---

## ⚡ Comandos de Agente

- `/refactor-to-record [class]` → Convierte DTO/ValueObject a `record` con validación en compact constructor
- `/async-composition [flow]` → Genera cadena `CompletableFuture` atómica para flujo dado
- `/modernize-java [file]` → Aplica `var`, pattern matching, switch expressions, y `.toList()`
- `/check-async-safety` → Detecta `.get()`, `.join()` o `@Transactional` en contextos async
- `/seal-domain [aggregate]` → Convierte jerarquía de dominio a `sealed interface/class` con exhaustividad
```
