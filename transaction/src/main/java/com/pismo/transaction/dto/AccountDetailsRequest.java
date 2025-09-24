package com.pismo.transaction.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AccountDetailsRequest(
        @NotEmpty(message = "documentNumber must not be blank")
        String documentNumber
) {}
