package com.pismo.transaction.service;

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
import com.pismo.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Test
    void createTransaction_shouldReturnResponse() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionService service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);
        account.setDocumentNumber("12345678900");

        OperationType payment = new OperationType();
        payment.setOperationTypeId(4);
        payment.setDescription("PAYMENT");
        payment.setDirection(OperationDirection.CREDIT);

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(4)).thenReturn(Optional.of(payment));

        Transaction tx = new Transaction();
        tx.setTransactionId(100L);
        tx.setAccount(account);
        tx.setOperationType(payment);
        tx.setAmount(BigDecimal.valueOf(123.45));

        when(txRepo.save(any(Transaction.class))).thenReturn(tx);

        TransactionRequest req = new TransactionRequest(1L, 4, BigDecimal.valueOf(123.45));
        TransactionResponse response = service.createTransaction(req);

        assertThat(response.transactionId()).isEqualTo(100L);
        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(BigDecimal.valueOf(123.45));
    }

    @Test
    void createTransaction_shouldThrow_whenAccountNotFound() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionService service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        when(accountRepo.findById(99L)).thenReturn(Optional.empty());

        TransactionRequest req = new TransactionRequest(99L, 4, BigDecimal.valueOf(50));

        assertThatThrownBy(() -> service.createTransaction(req))
                .isInstanceOf(AccountDetailsNotFoundException.class)
                .hasMessageContaining("Account with id 99 not found");
    }

    @Test
    void createTransaction_shouldThrow_whenOperationTypeNotFound() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionService service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);
        account.setDocumentNumber("12345678900");

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(99)).thenReturn(Optional.empty());

        TransactionRequest req = new TransactionRequest(1L, 99, BigDecimal.valueOf(75));

        assertThatThrownBy(() -> service.createTransaction(req))
                .isInstanceOf(OperationTypeNotFoundException.class) // or your custom exception
                .hasMessageContaining("Operation type with id 99 not found");
    }

    @Test
    void createTransaction_shouldHandleNegativeAmountForPurchase() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionService service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);

        OperationType purchase = new OperationType();
        purchase.setOperationTypeId(1);
        purchase.setDescription("PURCHASE");
        purchase.setDirection(OperationDirection.DEBIT);

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(1)).thenReturn(Optional.of(purchase));

        Transaction tx = new Transaction();
        tx.setTransactionId(200L);
        tx.setAccount(account);
        tx.setOperationType(purchase);
        tx.setAmount(BigDecimal.valueOf(-123.45));

        when(txRepo.save(any(Transaction.class))).thenReturn(tx);

        TransactionRequest req = new TransactionRequest(1L, 1, BigDecimal.valueOf(123.45));
        TransactionResponse response = service.createTransaction(req);

        assertThat(response.amount()).isEqualTo(BigDecimal.valueOf(-123.45));
    }

    @Test
    void createTransaction_shouldThrow_whenAmountIsZero() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionService service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);
        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));

        OperationType op = new OperationType();
        op.setOperationTypeId(2);
        op.setDescription("INSTALLMENT");
        op.setDirection(OperationDirection.DEBIT);

        when(opRepo.findById(2)).thenReturn(Optional.of(op));

        TransactionRequest req = new TransactionRequest(1L, 2, BigDecimal.ZERO);

        assertThatThrownBy(() -> service.createTransaction(req))
                .isInstanceOf(TransactionException.class) // or custom exception
                .hasMessageContaining("Amount must be greater than zero");
    }
    @Test
    void createTransaction_shouldThrow_whenSaveFails() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionService service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);
        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));

        OperationType op = new OperationType();
        op.setOperationTypeId(3);
        op.setDescription("WITHDRAWAL");
        op.setDirection(OperationDirection.DEBIT);

        when(opRepo.findById(3)).thenReturn(Optional.of(op));

        when(txRepo.save(any(Transaction.class))).thenThrow(new RuntimeException("DB error"));

        TransactionRequest req = new TransactionRequest(1L, 3, BigDecimal.valueOf(20));

        assertThatThrownBy(() -> service.createTransaction(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB error");
    }



}
