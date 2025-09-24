package com.pismo.transaction.repository;



import com.pismo.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}