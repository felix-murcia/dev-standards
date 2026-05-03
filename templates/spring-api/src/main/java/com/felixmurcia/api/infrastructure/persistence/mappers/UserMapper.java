// templates/spring-api/src/main/java/com/felixmurcia/api/infrastructure/persistence/mappers/UserMapper.java
package com.felixmurcia.api.infrastructure.persistence.mappers;
import com.felixmurcia.api.domain.entities.User;
import com.felixmurcia.api.domain.valueobjects.Email;
import com.felixmurcia.api.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User toDomain(UserJpaEntity e) {
        return new User(e.getId(), new Email(e.getEmail()), e.getPasswordHash(), e.getName(), e.getCreatedAt());
    }
    public UserJpaEntity toJpa(User d) {
        return new UserJpaEntity(d.id(), d.email().value(), d.passwordHash(), d.name(), d.createdAt());
    }
}
