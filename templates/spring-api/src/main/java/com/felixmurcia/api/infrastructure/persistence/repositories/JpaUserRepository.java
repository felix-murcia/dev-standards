// templates/spring-api/src/main/java/com/felixmurcia/api/infrastructure/persistence/repositories/JpaUserRepository.java
package com.felixmurcia.api.infrastructure.persistence.repositories;
import com.felixmurcia.api.infrastructure.persistence.entities.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaUserRepository extends JpaRepository<UserJpaEntity, UUID> {
    boolean existsByEmail(String email);
}
