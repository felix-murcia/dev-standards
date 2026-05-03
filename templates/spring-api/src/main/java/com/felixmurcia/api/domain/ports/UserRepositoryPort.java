// templates/spring-api/src/main/java/com/felixmurcia/api/domain/ports/UserRepositoryPort.java
package com.felixmurcia.api.domain.ports;
import com.felixmurcia.api.domain.entities.User;
import com.felixmurcia.api.domain.valueobjects.Email;
import java.util.Optional;
import java.util.UUID;
public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    boolean existsByEmail(Email email);
}
