// templates/spring-api/src/main/java/com/felixmurcia/api/infrastructure/persistence/repositories/UserRepositoryImpl.java
package com.felixmurcia.api.infrastructure.persistence.repositories;
import com.felixmurcia.api.domain.entities.User;
import com.felixmurcia.api.domain.ports.UserRepositoryPort;
import com.felixmurcia.api.domain.valueobjects.Email;
import com.felixmurcia.api.infrastructure.persistence.mappers.UserMapper;
import com.felixmurcia.api.infrastructure.persistence.repositories.JpaUserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepositoryPort {
    private final JpaUserRepository jpa;
    private final UserMapper mapper;
    public UserRepositoryImpl(JpaUserRepository jpa, UserMapper mapper) { this.jpa = jpa; this.mapper = mapper; }

    @Override @Transactional
    public User save(User user) { return mapper.toDomain(jpa.save(mapper.toJpa(user))); }

    @Override
    public Optional<User> findById(UUID id) { return jpa.findById(id).map(mapper::toDomain); }

    @Override
    public boolean existsByEmail(Email email) { return jpa.existsByEmail(email.value()); }
}
