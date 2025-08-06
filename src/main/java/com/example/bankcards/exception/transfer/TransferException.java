package com.example.bankcards.exception.transfer;

public class TransferException extends RuntimeException {
    private final int httpStatus;

    public TransferException(String message, int httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}