package com.pismo.transaction.exception;

public class AccountDetailsAlreadyExistsException extends RuntimeException {
    public AccountDetailsAlreadyExistsException(String documentNumber) {
        super("Account with documentNumber " + documentNumber + " already exists");
    }
}
