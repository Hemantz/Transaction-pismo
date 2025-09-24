package com.pismo.transaction.repository;

import com.pismo.transaction.entity.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OperationTypeRepository extends JpaRepository<OperationType, Integer> {
}