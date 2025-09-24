package com.pismo.transaction.exception;

public class AccountDetailsNotFoundException extends RuntimeException {
    public AccountDetailsNotFoundException(Long id) {
        super("Account with id " + id + " not found");
    }
}

