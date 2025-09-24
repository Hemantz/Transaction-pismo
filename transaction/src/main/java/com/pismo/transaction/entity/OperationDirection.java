package com.pismo.transaction.entity;

public enum OperationDirection {
    DEBIT(-1),
    CREDIT(1);

    private final int sign;

    OperationDirection(int sign) {
        this.sign = sign;
    }

    public int getSign() {
        return sign;
    }
}