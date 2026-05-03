// templates/spring-api/src/main/java/com/felixmurcia/api/presentation/controllers/UserController.java
package com.felixmurcia.api.presentation.controllers;
import com.felixmurcia.api.application.dtos.CreateUserRequest;
import com.felixmurcia.api.application.dtos.UserResponse;
import com.felixmurcia.api.application.usecases.CreateUserUseCase;
import com.felixmurcia.api.presentation.schemas.UserCreateSchema;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final CreateUserUseCase useCase;
    public UserController(CreateUserUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    public CompletableFuture<ResponseEntity<UserResponse>> create(@Valid @RequestBody UserCreateSchema schema) {
        return useCase.executeAsync(new CreateUserRequest(schema.email(), schema.password(), schema.name()))
                      .thenApply(ResponseEntity::status(HttpStatus.CREATED));
    }
}
