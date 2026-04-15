package org.example.springtesttask.exception;

public class NoSuchWalletException extends RuntimeException{
    public NoSuchWalletException() {
        super("Кошелёк с данным walletId отсутствует в системе");
    }
}
