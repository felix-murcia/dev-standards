// templates/spring-api/src/main/java/com/felixmurcia/api/presentation/schemas/UserCreateSchema.java
package com.felixmurcia.api.presentation.schemas;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record UserCreateSchema(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) String password,
    @NotBlank String name
) {}
