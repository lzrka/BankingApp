package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.AccountRepository;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService{

  @Autowired
  AccountRepository accountRepository;

  public List<Account> getAllAccounts() {
    return accountRepository.findAll();
  }

  public Account getAccountById(Integer id) {
    Objects.requireNonNull(id);
    return accountRepository.findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Account with id: " + id + " doesn't exist"));

  }

  @Transactional
  public Account createAccount(Account account) {
    Objects.requireNonNull(account);
    if (account.getId() != null && existsById(account.getId())) {
      throw new ResourceAlreadyExistsException(
          "Account with id: " + account.getId() + " already exists");
    }
    while (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
      account.setAccountNumber(Account.generateAccountNumber());
    }
    return accountRepository.save(account);
  }

  @Transactional
  public Account updateAccount(Account account) {
    Objects.requireNonNull(account);
    if (!existsById(account.getId())) {
      throw new ResourceNotFoundException("Account with id: " + account.getId() + " doesn't exist");
    } else {
      return accountRepository.save(account);
    }
  }

  @Transactional
  public void removeAccount(Integer id) {
    Objects.requireNonNull(id);
    if (!existsById(id)) {
      throw new ResourceNotFoundException("Account with id: " + id + " doesn't exist ");
    }
    accountRepository.deleteById(id);
  }

  public Account getAccountByAccountNumber(String accountNumber) {
    return accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(
            () -> new ResourceNotFoundException("Account with accountNumber: " + accountNumber + " doesn't exist"));
  }

  private boolean existsById(Integer id) {
    return accountRepository.existsById(id);
  }
}
