package com.pismo.transaction.controller;

import com.pismo.transaction.dto.TransactionRequest;
import com.pismo.transaction.dto.TransactionResponse;
import com.pismo.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling transaction-related operations.
 * Provides endpoints to create transactions.
 */
@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Creates a new transaction for a given account and operation type.
     *
     * @param request the transaction request containing accountId, operationTypeId, and amount
     * @return ResponseEntity containing the created TransactionResponse
     */
    @PostMapping("/create")
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest request) {
        log.info("Received request to create transaction: accountId={}, operationTypeId={}, amount={}",
                request.accountId(), request.operationTypeId(), request.amount());

        TransactionResponse response = transactionService.createTransaction(request);

        log.info("Transaction created successfully with transactionId={}, accountId={}",
                response.transactionId(), response.accountId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
