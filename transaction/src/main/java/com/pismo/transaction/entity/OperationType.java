package com.pismo.transaction.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "operation_types")
public class OperationType {
    @Id
    private Integer operationTypeId;

    private String description;

    @Enumerated(EnumType.STRING) // stores "DEBIT" or "CREDIT" in DB
    private OperationDirection direction;
}