package com.example.bankcards.exception.card;

public class CardNotFoundException extends RuntimeException {
    private final int httpStatus;

    public CardNotFoundException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getStatus() {
        return httpStatus;
    }
}