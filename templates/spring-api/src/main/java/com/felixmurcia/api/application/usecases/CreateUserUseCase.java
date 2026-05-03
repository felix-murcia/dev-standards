// templates/spring-api/src/main/java/com/felixmurcia/api/application/usecases/CreateUserUseCase.java
package com.felixmurcia.api.application.usecases;
import com.felixmurcia.api.application.dtos.CreateUserRequest;
import com.felixmurcia.api.application.dtos.UserResponse;
import com.felixmurcia.api.domain.entities.User;
import com.felixmurcia.api.domain.exceptions.UserAlreadyExistsException;
import com.felixmurcia.api.domain.ports.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Service
public class CreateUserUseCase {
    private final UserRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    public CreateUserUseCase(UserRepositoryPort repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public CompletableFuture<UserResponse> executeAsync(CreateUserRequest request) {
        var checkExists = CompletableFuture.supplyAsync(() -> {
            if (repository.existsByEmail(new com.felixmurcia.api.domain.valueobjects.Email(request.email())))
                throw new UserAlreadyExistsException(request.email());
            return true;
        });

        var createEntity = CompletableFuture.supplyAsync(() ->
            User.create(new com.felixmurcia.api.domain.valueobjects.Email(request.email()),
                        passwordEncoder.encode(request.password()), request.name()));

        return checkExists.thenCombine(createEntity, (_, user) -> user)
                          .thenApplyAsync(repository::save)
                          .thenApplyAsync(UserResponse::fromEntity)
                          .exceptionally(ex -> { throw new RuntimeException(ex.getCause()); });
    }
}
