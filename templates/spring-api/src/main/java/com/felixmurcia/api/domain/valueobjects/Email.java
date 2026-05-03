// templates/spring-api/src/main/java/com/felixmurcia/api/domain/valueobjects/Email.java
package com.felixmurcia.api.domain.valueobjects;
public record Email(String value) {
    public Email {
        var pattern = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        if (!value.matches(pattern)) throw new IllegalArgumentException("Invalid email format");
        value = value.toLowerCase().trim();
    }
}
