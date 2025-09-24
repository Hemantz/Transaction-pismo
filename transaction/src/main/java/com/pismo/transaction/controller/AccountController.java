package com.pismo.transaction.controller;

import com.pismo.transaction.dto.AccountDetailsRequest;
import com.pismo.transaction.dto.AccountResponse;
import com.pismo.transaction.service.AccountService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for account operations.
 */
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    /**
     * Creates a new account.
     *
     * @param request the account request payload
     * @return created account response with 201 status
     */
    @PostMapping("/create")
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountDetailsRequest request) {
        log.info("Received request to create account with documentNumber={}", request.documentNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(request));
    }


    /**
     * Retrieves an account by its ID.
     *
     * @param id the account ID
     * @return account details with 200 status
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> get(@Valid @PathVariable Long id) {
        log.info("Received request to fetch account with id={}", id);
        return ResponseEntity.ok(accountService.getAccount(id));
    }
}
