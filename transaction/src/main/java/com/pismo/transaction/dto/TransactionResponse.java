package com.pismo.transaction.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;


public record TransactionResponse(
        Long transactionId,
        Long accountId,
        Integer operationTypeId,
        BigDecimal amount,
        OffsetDateTime eventDate) {}
