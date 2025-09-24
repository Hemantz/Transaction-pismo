package com.pismo.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.transaction.dto.AccountDetailsRequest;
import com.pismo.transaction.dto.AccountResponse;
import com.pismo.transaction.exception.AccountDetailsNotFoundException;
import com.pismo.transaction.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AccountResponse sampleResponse;

    @BeforeEach
    void setup() {
        sampleResponse = new AccountResponse(1L, "12345678900");
    }

    @Test
    void createAccount_success() throws Exception {
        AccountDetailsRequest request = new AccountDetailsRequest("12345678900");

        Mockito.when(accountService.createAccount(any(AccountDetailsRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.documentNumber").value("12345678900"));
    }

    @Test
    void getAccount_success() throws Exception {
        Mockito.when(accountService.getAccount(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/v1/accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.documentNumber").value("12345678900"));
    }

    @Test
    void getAccount_notFound() throws Exception {
        Mockito.when(accountService.getAccount(99L)).thenThrow(new AccountDetailsNotFoundException(99L));
        mockMvc.perform(get("/api/v1/accounts/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Account with id 99 not found"));
    }

}
