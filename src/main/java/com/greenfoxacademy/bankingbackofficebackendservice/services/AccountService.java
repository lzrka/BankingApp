package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import java.util.List;

public interface AccountService {

  List<Account> getAllAccounts();

  Account getAccountById(Integer id);

  Account createAccount(Account account);

  Account updateAccount(Account account);

  void removeAccount(Integer id);

  Account getAccountByAccountNumber(String targetAccountNumber);
}
