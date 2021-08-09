package com.greenfoxacademy.bankingbackofficebackendservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.AccountRepository;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class AccountServiceTest {

  @Autowired
  private AccountService accountService;

  @MockBean
  private AccountRepository accountRepository;

  @Test
  public void getAllAccountsTestSuccess() {
    Client client1 = Client.builder().id(0).build();
    Client client2 = Client.builder().id(1).build();

    List<Account> accounts = new ArrayList<>();

    Account account0 = Account.builder()
        .id(1)
        .accountNumber("112233445566778899112233")
        .client(client1)
        .currency(Currency.getInstance("HUF"))
        .value(1000L)
        .build();
    Account account1 = Account.builder()
        .id(2)
        .accountNumber("112233445566778811223344")
        .client(client2)
        .currency(Currency.getInstance("USD"))
        .value(2000L)
        .build();

    accounts.add(account0);
    accounts.add(account1);

    when(accountRepository.findAll()).thenReturn(accounts);

    List<Account> expectedList = accountService.getAllAccounts();

    assertEquals(2, expectedList.size());
    assertEquals(expectedList, accounts);
    verify(accountRepository, times(1)).findAll();
  }

  @Test
  public void getAccountByIdTestSuccess() {
    Client client1 = Client.builder().id(0).build();
    Account account0 = Account.builder()
        .id(1)
        .accountNumber("112233445566778899112233")
        .client(client1)
        .currency(Currency.getInstance("HUF"))
        .value(1000L)
        .build();

    when(accountRepository.findById(1)).thenReturn(Optional.of(account0));

    Account account = accountService.getAccountById(1);

    assertEquals("112233445566778899112233", account.getAccountNumber());
    assertEquals(client1, account.getClient());
    assertEquals(Currency.getInstance("HUF"), account.getCurrency());
    assertEquals(1000L, account.getValue());
  }

  @Test
  public void getAccountByIdTestDoesntExist() {
    Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.getAccountById(1));
    assertEquals("Account with id: 1 doesn't exist", exception.getMessage());
  }

  @Test
  public void createAccountTestSuccess() {
    Client client1 = Client.builder().id(0).build();
    Account account0 = Account.builder()
        .id(1)
        .accountNumber("112233445566778899112233")
        .client(client1)
        .currency(Currency.getInstance("HUF"))
        .value(1000L)
        .build();

    when(accountRepository.save(account0)).thenReturn(account0);

    Account account = accountService.createAccount(account0);

    assertEquals("112233445566778899112233", account.getAccountNumber());
    assertEquals(client1, account.getClient());
    assertEquals(Currency.getInstance("HUF"), account.getCurrency());
    assertEquals(1000L, account.getValue());
  }

  @Test
  public void createAccountTestAlreadyExists() {
    Client client1 = Client.builder().id(0).build();
    Account account0 = Account.builder()
        .id(1)
        .accountNumber("112233445566778899112233")
        .client(client1)
        .currency(Currency.getInstance("HUF"))
        .value(1000L)
        .build();

    when(accountRepository.existsById(account0.getId())).thenReturn(true);

    Exception exception = Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> accountService.createAccount(account0));
    assertEquals("Account with id: 1 already exists", exception.getMessage());
  }

  @Test
  public void updateAccountTestSuccess() {
    Client client1 = Client.builder().id(0).build();
    Account account0 = Account.builder()
        .id(1)
        .accountNumber("112233445566778899112233")
        .client(client1)
        .currency(Currency.getInstance("HUF"))
        .value(1000L)
        .build();

    when(accountRepository.existsById(account0.getId())).thenReturn(true);
    when(accountRepository.save(account0)).thenReturn(account0);

    Account updateAccount = accountService.updateAccount(account0);
    assertEquals(updateAccount, account0);
  }

  @Test
  public void updateAccountTestDoesntExist() {
    Client client1 = Client.builder().id(0).build();
    Account account0 = Account.builder()
        .accountNumber("112233445566778899112233")
        .client(client1)
        .currency(Currency.getInstance("HUF"))
        .value(1000L)
        .build();

    when(accountRepository.existsById(account0.getId())).thenReturn(false);

    Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.updateAccount(account0));
    assertEquals("Account with id: null doesn't exist", exception.getMessage());
  }

  @Test
  public void removeAccountTestSuccess() {
    when(accountRepository.existsById(1)).thenReturn(true);
    accountService.removeAccount(1);
    verify(accountRepository, times(1)).deleteById(1);
  }

  @Test
  public void removeAccountTestDoesntExist() {
    when(accountRepository.existsById(1)).thenReturn(false);
    Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> accountService.removeAccount(1));
    assertEquals("Account with id: 1 doesn't exist ", exception.getMessage());
  }
}
