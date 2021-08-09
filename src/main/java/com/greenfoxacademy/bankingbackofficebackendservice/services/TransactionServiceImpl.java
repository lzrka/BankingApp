package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.TransactionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService{

  @Autowired
  private TransactionRepository transactionRepository;

  @Override
  public List<Transaction> getAllTransactions() {
    return transactionRepository.findAllByDateAfter(ninetyDays());
  }

  @Override
  public Transaction createTransaction(Transaction transaction) {
    Objects.requireNonNull(transaction);
    if (transaction.getId() != null && existsById(transaction.getId())) {
      throw new ResourceAlreadyExistsException(
          "Transaction with id: " + transaction.getId() + " already exists");
    }

    return transactionRepository.save(transaction);
  }

  @Override
  public List<Transaction> getAllTransactionsFromSourceAccount(String sourceAccount) {
    return transactionRepository
        .findTransactionBySourceAccountNumberAndDateAfter(sourceAccount, ninetyDays());
  }

  @Override
  public List<Transaction> getAllTransactionsFromTargetAccount(String targetAccount) {
    return transactionRepository
        .findTransactionByTargetAccountNumberAndDateAfter(targetAccount, ninetyDays());
  }

  @Override
  public List<Transaction> getAllTransactionsFromSourceAccountAndTargetAccount(String sourceAccount,
                                                                               String targetAccount) {
    return transactionRepository
        .findTransactionBySourceAccountNumberAndTargetAccountNumberAndDateAfter(sourceAccount,
            targetAccount, ninetyDays());
  }

  private boolean existsById(Integer id) {
    return transactionRepository.existsById(id);
  }

  private LocalDateTime ninetyDays() {
    return LocalDateTime.now().minusDays(90);
  }

}
