// templates/spring-api/src/main/java/com/felixmurcia/api/presentation/exceptions/GlobalExceptionHandler.java
package com.felixmurcia.api.presentation.exceptions;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    record ErrorPayload(LocalDateTime timestamp, String error, String message) {}

    @ExceptionHandler(com.felixmurcia.api.domain.exceptions.UserAlreadyExistsException.class)
    public ResponseEntity<ErrorPayload> handleConflict(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                             .body(new ErrorPayload(LocalDateTime.now(), "CONFLICT", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorPayload> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ErrorPayload(LocalDateTime.now(), "INTERNAL_ERROR", "Unexpected failure"));
    }
}
