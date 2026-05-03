// templates/spring-api/src/main/java/com/felixmurcia/api/domain/exceptions/UserAlreadyExistsException.java
package com.felixmurcia.api.domain.exceptions;
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) { super("User already exists: " + email); }
}
