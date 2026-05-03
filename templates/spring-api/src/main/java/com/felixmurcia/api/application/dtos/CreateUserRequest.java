// templates/spring-api/src/main/java/com/felixmurcia/api/application/dtos/CreateUserRequest.java
package com.felixmurcia.api.application.dtos;
public record CreateUserRequest(String email, String password, String name) {}
