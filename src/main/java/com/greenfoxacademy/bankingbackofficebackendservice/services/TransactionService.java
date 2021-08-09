package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import java.util.List;

public interface TransactionService {

  List<Transaction> getAllTransactions();

  Transaction createTransaction(Transaction transaction);

  List<Transaction> getAllTransactionsFromSourceAccount(String sourceAccount);

  List<Transaction> getAllTransactionsFromTargetAccount(String targetAccount);

  List<Transaction> getAllTransactionsFromSourceAccountAndTargetAccount(String sourceAccount, String targetAccount);
}
