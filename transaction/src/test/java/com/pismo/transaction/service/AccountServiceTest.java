package com.pismo.transaction.service;

import com.pismo.transaction.dto.AccountDetailsRequest;
import com.pismo.transaction.dto.AccountResponse;
import com.pismo.transaction.entity.Account;
import com.pismo.transaction.repository.AccountRepository;
import com.pismo.transaction.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.Test;


import java.util.Optional;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class AccountServiceTest {


    @Test
    void createAccount_shouldReturnResponse() {
        AccountRepository repo = mock(AccountRepository.class);
        AccountService service = new AccountServiceImpl(repo);


        Account account = new Account();
        account.setAccountId(1L);
        account.setDocumentNumber("12345678900");


        when(repo.save(any(Account.class))).thenReturn(account);


        AccountResponse response = service.createAccount(new AccountDetailsRequest("12345678900"));


        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.documentNumber()).isEqualTo("12345678900");
    }


    @Test
    void getAccount_shouldReturnResponse() {
        AccountRepository repo = mock(AccountRepository.class);
        AccountService service = new AccountServiceImpl(repo);


        Account account = new Account();
        account.setAccountId(1L);
        account.setDocumentNumber("12345678900");


        when(repo.findById(1L)).thenReturn(Optional.of(account));


        AccountResponse response = service.getAccount(1L);
        assertThat(response.accountId()).isEqualTo(1L);
        assertThat(response.documentNumber()).isEqualTo("12345678900");
    }
}