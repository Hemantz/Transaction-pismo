package com.pismo.transaction.exception;

public class OperationTypeNotFoundException extends RuntimeException {
    public OperationTypeNotFoundException(Integer id) {
        super("Operation type with id " + id + " not found");
    }
}
