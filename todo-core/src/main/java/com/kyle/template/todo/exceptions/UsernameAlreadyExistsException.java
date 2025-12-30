package com.kyle.template.todo.exceptions;

import org.springframework.http.HttpStatus;


/**
 * Exception thrown when username already exists (1.1.2)
 */
public class UsernameAlreadyExistsException extends AppException {

    public UsernameAlreadyExistsException(String username) {
        super("Username already registered: " + username, HttpStatus.CONFLICT, "USERNAME_EXISTS");
    }
}