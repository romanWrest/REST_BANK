package com.example.bankcards.exception.user;

public class UserNotFoundException extends RuntimeException {
    private final int httpStatus;

    public UserNotFoundException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getStatus() {
        return httpStatus;
    }
}