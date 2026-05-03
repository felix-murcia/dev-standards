// templates/spring-api/src/main/java/com/felixmurcia/api/application/dtos/UserResponse.java
package com.felixmurcia.api.application.dtos;
import com.felixmurcia.api.domain.entities.User;
import java.time.LocalDateTime;
import java.util.UUID;
public record UserResponse(UUID id, String email, String name, LocalDateTime createdAt) {
    public static UserResponse fromEntity(User user) {
        return new UserResponse(user.id(), user.email().value(), user.name(), user.createdAt());
    }
}
