// templates/spring-api/src/test/java/com/felixmurcia/api/application/usecases/CreateUserUseCaseTest.java
package com.felixmurcia.api.application.usecases;
import com.felixmurcia.api.application.dtos.CreateUserRequest;
import com.felixmurcia.api.domain.entities.User;
import com.felixmurcia.api.domain.exceptions.UserAlreadyExistsException;
import com.felixmurcia.api.domain.ports.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateUserUseCaseTest {
    @Mock UserRepositoryPort repo;
    @Mock PasswordEncoder encoder;
    @InjectMocks CreateUserUseCase useCase;

    @Test void shouldCreateUserWhenEmailNotExists() {
        when(encoder.encode("pass123")).thenReturn("$2a$...");
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        
        var req = new CreateUserRequest("test@domain.com", "pass123", "Alex");
        CompletableFuture<?> future = useCase.executeAsync(req);
        assertDoesNotThrow(() -> future.join());
        verify(repo).existsByEmail(any());
        verify(repo).save(any());
    }

    @Test void shouldThrowWhenEmailExists() {
        when(repo.existsByEmail(any())).thenReturn(true);
        var req = new CreateUserRequest("dup@domain.com", "pass123", "Alex");
        assertThrows(RuntimeException.class, () -> useCase.executeAsync(req).join());
    }
}
