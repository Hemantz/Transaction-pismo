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
import java.util.List;
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

        Transaction tx = getTransaction(accountRepo, opRepo);

        when(txRepo.save(any(Transaction.class))).thenReturn(tx);

        TransactionRequest req = new TransactionRequest(1L, 4, BigDecimal.valueOf(123.45));
        TransactionResponse response = service.createTransaction(req);

        assertThat(response.transactionId()).isEqualTo(100L);
        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.amount()).isEqualTo(BigDecimal.valueOf(123.45));
    }


    private static Transaction getTransaction(AccountRepository accountRepo, OperationTypeRepository opRepo) {
        OperationType payment = new OperationType();
        payment.setOperationTypeId(4);
        payment.setDescription("PAYMENT");
        payment.setDirection(OperationDirection.CREDIT);

        Account account = new Account();
        account.setAccountId(1L);
        account.setDocumentNumber("12345678900");

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(4)).thenReturn(Optional.of(payment));

        Transaction tx = new Transaction();
        tx.setTransactionId(100L);
        tx.setAccount(account);
        tx.setBalance(BigDecimal.valueOf(60));
        tx.setOperationType(payment);
        tx.setAmount(BigDecimal.valueOf(123.45));
        return tx;
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

    @Test
    void createTransaction_shouldDischargeDebitsProperly() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionServiceImpl service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);

        OperationType payment = new OperationType();
        payment.setOperationTypeId(4);
        payment.setDirection(OperationDirection.CREDIT);

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(4)).thenReturn(Optional.of(payment));

        // Existing debit transactions
        Transaction debit1 = new Transaction();
        debit1.setTransactionId(101L);
        debit1.setAccount(account);
        debit1.setAmount(BigDecimal.valueOf(-50));  // negative debit
        debit1.setBalance(BigDecimal.valueOf(-50));

        Transaction debit2 = new Transaction();
        debit2.setTransactionId(102L);
        debit2.setAccount(account);
        debit2.setAmount(BigDecimal.valueOf(-30));  // negative debit
        debit2.setBalance(BigDecimal.valueOf(-30));

        when(txRepo.findAllDebitTransaction(1L)).thenReturn(List.of(debit1, debit2));

        // Save payment transaction
        Transaction paymentTx = new Transaction();
        paymentTx.setTransactionId(200L);
        paymentTx.setAccount(account);
        paymentTx.setAmount(BigDecimal.valueOf(60));  // payment amount
        paymentTx.setBalance(BigDecimal.valueOf(60));

        Transaction tx = getTransaction(accountRepo, opRepo);
        when(txRepo.save(any(Transaction.class))).thenReturn(tx);

        TransactionRequest req = new TransactionRequest(1L, 4, BigDecimal.valueOf(60));
        service.createTransaction(req);

        // Verify debits are partially discharged
        assertThat(debit1.getBalance()).isEqualTo(BigDecimal.valueOf(0)); // fully discharged
        assertThat(debit2.getBalance()).isEqualTo(BigDecimal.valueOf(-20)); // partially discharged

        // Verify payment balance updated
        assertThat(paymentTx.getBalance()).isEqualTo(BigDecimal.valueOf(60));

        // Verify repository save calls
        verify(txRepo, atLeast(3)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_shouldHandlePaymentWhenNoDebitsExist() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionServiceImpl service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Transaction tx = getTransaction(accountRepo, opRepo);

        when(txRepo.save(any(Transaction.class))).thenReturn(tx);

        Account account = new Account();
        account.setAccountId(1L);

        OperationType payment = new OperationType();
        payment.setOperationTypeId(4);
        payment.setDirection(OperationDirection.CREDIT);

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(4)).thenReturn(Optional.of(payment));
        when(txRepo.findAllDebitTransaction(1L)).thenReturn(List.of()); // no debits

        TransactionRequest req = new TransactionRequest(tx.getAccount().getAccountId(), 4, tx.getAmount());
        TransactionResponse transaction = service.createTransaction(req);

        assertThat(transaction.amount()).isEqualTo(BigDecimal.valueOf(123.45));
    }

    @Test
    void createTransaction_shouldPartiallyDischargeDebit() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionServiceImpl service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);

        OperationType payment = new OperationType();
        payment.setOperationTypeId(4);
        payment.setDirection(OperationDirection.CREDIT);

        Transaction debit = new Transaction();
        debit.setTransactionId(101L);
        debit.setAccount(account);
        debit.setBalance(BigDecimal.valueOf(-200));

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(4)).thenReturn(Optional.of(payment));
        when(txRepo.findAllDebitTransaction(1L)).thenReturn(List.of(debit));
        Transaction tx = getTransaction(accountRepo, opRepo);
        when(txRepo.save(any(Transaction.class))).thenReturn(tx);

        TransactionRequest req = new TransactionRequest(1L, 4, BigDecimal.valueOf(50));
        service.createTransaction(req);

        // Debit partially discharged
        assertThat(debit.getBalance()).isEqualTo(BigDecimal.valueOf(-140));
    }
    @Test
    void createTransaction_shouldNotDischargeAlreadyClearedDebits() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionServiceImpl service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);

        OperationType payment = new OperationType();
        payment.setOperationTypeId(4);
        payment.setDirection(OperationDirection.CREDIT);

        Transaction debit = new Transaction();
        debit.setTransactionId(101L);
        debit.setAccount(account);
        debit.setBalance(BigDecimal.ZERO); // already cleared

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(4)).thenReturn(Optional.of(payment));
        when(txRepo.findAllDebitTransaction(1L)).thenReturn(List.of(debit));
        Transaction tx = getTransaction(accountRepo, opRepo);
        when(txRepo.save(any(Transaction.class))).thenReturn(tx);
        TransactionRequest req = new TransactionRequest(1L, 4, BigDecimal.valueOf(50));
        service.createTransaction(req);

        assertThat(debit.getBalance()).isEqualTo(BigDecimal.ZERO); // unchanged
    }
    @Test
    void createTransaction_shouldThrowWhenPaymentSaveFails() {
        AccountRepository accountRepo = mock(AccountRepository.class);
        OperationTypeRepository opRepo = mock(OperationTypeRepository.class);
        TransactionRepository txRepo = mock(TransactionRepository.class);

        TransactionServiceImpl service = new TransactionServiceImpl(accountRepo, opRepo, txRepo);

        Account account = new Account();
        account.setAccountId(1L);

        OperationType payment = new OperationType();
        payment.setOperationTypeId(4);
        payment.setDirection(OperationDirection.CREDIT);

        when(accountRepo.findById(1L)).thenReturn(Optional.of(account));
        when(opRepo.findById(4)).thenReturn(Optional.of(payment));
        when(txRepo.findAllDebitTransaction(1L)).thenReturn(List.of());
        when(txRepo.save(any(Transaction.class))).thenThrow(new RuntimeException("DB down"));

        TransactionRequest req = new TransactionRequest(1L, 4, BigDecimal.valueOf(50));
        assertThatThrownBy(() -> service.createTransaction(req))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("DB down");
    }
}
