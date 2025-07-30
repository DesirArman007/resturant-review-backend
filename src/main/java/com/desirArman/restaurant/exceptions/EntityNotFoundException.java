package com.desirArman.restaurant.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException() { }

    public EntityNotFoundException(Throwable cause) {
        super(cause);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
