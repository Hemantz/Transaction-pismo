package com.pismo.transaction.service.impl;

import com.pismo.transaction.dto.AccountDetailsRequest;
import com.pismo.transaction.dto.AccountResponse;
import com.pismo.transaction.entity.Account;
import com.pismo.transaction.exception.AccountDetailsAlreadyExistsException;
import com.pismo.transaction.exception.AccountDetailsNotFoundException;
import com.pismo.transaction.repository.AccountRepository;
import com.pismo.transaction.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service implementation for account-related operations.
 * Handles creation and retrieval of accounts.
 */
@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountRepository repository;

    public AccountServiceImpl(AccountRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates a new account if it does not already exist.
     *
     * @param request the {@link AccountDetailsRequest} containing the document number
     * @return {@link AccountResponse} with created account details
     * @throws AccountDetailsAlreadyExistsException if the account already exists
     */
    @Override
    public AccountResponse createAccount(AccountDetailsRequest request) {
        log.info("Creating account with documentNumber={}", request.documentNumber());

        // Check if an account with the same document number already exists
        Optional<Account> existingAccount = repository.findByDocumentNumber(request.documentNumber());

        if (existingAccount.isPresent()) {
            throw new AccountDetailsAlreadyExistsException(request.documentNumber());
        }

        Account account = new Account();
        account.setDocumentNumber(request.documentNumber());
        account = repository.save(account);

        log.info("Account created with accountId={}", account.getAccountId());
        return new AccountResponse(account.getAccountId(), account.getDocumentNumber());
    }



    /**
     * Retrieves an account by ID.
     *
     * @param id the account ID
     * @return AccountResponse with account details
     * @throws AccountDetailsNotFoundException if no account exists with the given ID
     */
    @Override
    public AccountResponse getAccount(Long id) {
        log.info("Fetching account with accountId={}", id);

        Account account = repository.findById(id)
                .orElseThrow(() -> new AccountDetailsNotFoundException(id));

        return new AccountResponse(account.getAccountId(), account.getDocumentNumber());
    }
}
