// templates/spring-api/src/main/java/com/felixmurcia/api/infrastructure/persistence/entities/UserJpaEntity.java
package com.felixmurcia.api.infrastructure.persistence.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {
    @Id private UUID id;
    @Column(nullable = false, unique = true) private String email;
    @Column(name = "password_hash", nullable = false) private String passwordHash;
    private String name;
    @Column(name = "created_at", updatable = false) private LocalDateTime createdAt;

    public UserJpaEntity() {}
    public UserJpaEntity(UUID id, String email, String passwordHash, String name, LocalDateTime createdAt) {
        this.id = id; this.email = email; this.passwordHash = passwordHash; this.name = name; this.createdAt = createdAt;
    }
    // Getters & Setters (required by JPA)
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
