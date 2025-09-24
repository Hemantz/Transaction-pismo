package com.pismo.transaction.service;


import com.pismo.transaction.dto.TransactionRequest;
import com.pismo.transaction.dto.TransactionResponse;

public interface TransactionService {
    TransactionResponse createTransaction(TransactionRequest request);
}
