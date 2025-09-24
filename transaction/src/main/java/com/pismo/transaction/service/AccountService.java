package com.pismo.transaction.service;

import com.pismo.transaction.dto.AccountDetailsRequest;
import com.pismo.transaction.dto.AccountResponse;

public interface AccountService {
    AccountResponse createAccount(AccountDetailsRequest request);
    AccountResponse getAccount(Long id);
}
