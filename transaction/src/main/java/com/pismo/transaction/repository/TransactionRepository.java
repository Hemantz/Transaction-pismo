package com.pismo.transaction.repository;



import com.pismo.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT * FROM TRANSACTIONS WHERE ACCOUNT_ID = :accountId AND BALANCE < 0 ORDER BY Transaction_Id ASC", nativeQuery = true)
    List<Transaction> findAllDebitTransaction(Long accountId);
}
