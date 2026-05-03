// templates/spring-api/src/main/java/com/felixmurcia/api/domain/entities/User.java
package com.felixmurcia.api.domain.entities;
import com.felixmurcia.api.domain.valueobjects.Email;
import java.time.LocalDateTime;
import java.util.UUID;
public record User(UUID id, Email email, String passwordHash, String name, LocalDateTime createdAt) {
    public static User create(Email email, String passwordHash, String name) {
        return new User(UUID.randomUUID(), email, passwordHash, name, LocalDateTime.now());
    }
}
