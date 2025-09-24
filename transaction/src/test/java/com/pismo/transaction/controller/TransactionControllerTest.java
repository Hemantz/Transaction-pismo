package com.pismo.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transaction.dto.TransactionRequest;
import com.pismo.transaction.dto.TransactionResponse;
import com.pismo.transaction.exception.AccountDetailsNotFoundException;
import com.pismo.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    private TransactionRequest validRequest;
    private TransactionResponse validResponse;

    @BeforeEach
    void setUp() {
        validRequest = new TransactionRequest(1L, 4, new BigDecimal("100.0"));
        validResponse = new TransactionResponse(1L, 1L, 4, new BigDecimal("100.0"), OffsetDateTime.now());
    }

    @Test
    void createTransaction_shouldReturnCreated() throws Exception {
        Mockito.when(transactionService.createTransaction(validRequest)).thenReturn(validResponse);

        mockMvc.perform(post("/api/v1/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.transactionId").value(validResponse.transactionId()))
                .andExpect(jsonPath("$.accountId").value(validResponse.accountId()))
                .andExpect(jsonPath("$.operationTypeId").value(validResponse.operationTypeId()))
                .andExpect(jsonPath("$.amount").value(validResponse.amount()));
    }

    @Test
    void createTransaction_accountNotFound() throws Exception {
        Mockito.when(transactionService.createTransaction(validRequest))
                .thenThrow(new AccountDetailsNotFoundException(validRequest.accountId()));

        mockMvc.perform(post("/api/v1/transactions/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account with id " + validRequest.accountId() + " not found"));
    }
}
