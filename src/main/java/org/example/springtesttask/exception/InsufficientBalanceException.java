package org.example.springtesttask.exception;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException() {
        super("На счету недостаточно средств");
    }
}
