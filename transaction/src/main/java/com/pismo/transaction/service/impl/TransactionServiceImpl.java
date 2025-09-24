package com.pismo.transaction.service.impl;

import com.pismo.transaction.dto.TransactionRequest;
import com.pismo.transaction.dto.TransactionResponse;
import com.pismo.transaction.entity.Account;
import com.pismo.transaction.entity.OperationDirection;
import com.pismo.transaction.entity.OperationType;
import com.pismo.transaction.entity.Transaction;
import com.pismo.transaction.exception.AccountDetailsNotFoundException;
import com.pismo.transaction.exception.OperationTypeNotFoundException;
import com.pismo.transaction.exception.TransactionException;
import com.pismo.transaction.repository.AccountRepository;
import com.pismo.transaction.repository.OperationTypeRepository;
import com.pismo.transaction.repository.TransactionRepository;
import com.pismo.transaction.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final AccountRepository accountRepository;
    private final OperationTypeRepository operationTypeRepository;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(
            AccountRepository accountRepository,
            OperationTypeRepository operationTypeRepository,
            TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.operationTypeRepository = operationTypeRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * Creates a transaction for a given account and operation type.
     * Debit operations are stored as negative, Credit operations as positive.
     *
     * @param request the transaction request
     * @return the saved transaction response
     * @throws AccountDetailsNotFoundException if the account does not exist
     * @throws OperationTypeNotFoundException  if the operation type does not exist
     */
    @Override
    public TransactionResponse createTransaction(TransactionRequest request) {
        log.info("Creating transaction for accountId={} amount={} operationTypeId={}",
                request.accountId(), request.amount(), request.operationTypeId());

        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionException("Amount must be greater than zero");
        }

        Account account = accountRepository.findById(request.accountId())
                .orElseThrow(() -> new AccountDetailsNotFoundException(request.accountId()));

        OperationType opType = operationTypeRepository.findById(request.operationTypeId())
                .orElseThrow(() -> new OperationTypeNotFoundException(request.operationTypeId()));


        BigDecimal computedAmount = computeTransactionAmount(request.amount(), opType.getDirection());

        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setOperationType(opType);
        tx.setAmount(computedAmount);
        tx.setEventDate(OffsetDateTime.now());

        Transaction saved = transactionRepository.save(tx);

        log.info("Transaction created transactionId={} finalAmount={}", saved.getTransactionId(), saved.getAmount());

        return new TransactionResponse(
                saved.getTransactionId(),
                account.getAccountId(),
                opType.getOperationTypeId(),
                saved.getAmount(),
                saved.getEventDate()
        );
    }

    /**
     * Computes the signed transaction amount based on the operation type.
     *
     * @param amount    original transaction amount
     * @param direction CREDIT or DEBIT
     * @return signed amount
     */
    private BigDecimal computeTransactionAmount(BigDecimal amount, OperationDirection direction) {
        return direction == OperationDirection.DEBIT
                ? amount.abs().negate() // purchase, withdrawal
                : amount.abs();         // payment
    }
}
