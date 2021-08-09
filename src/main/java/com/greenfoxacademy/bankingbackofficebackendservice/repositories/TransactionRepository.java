package com.greenfoxacademy.bankingbackofficebackendservice.repositories;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

  List<Transaction> findAllByDateAfter(LocalDateTime date);
  List<Transaction> findTransactionBySourceAccountNumberAndDateAfter(String sourceAccount,
                                                                     LocalDateTime date);
  List<Transaction> findTransactionByTargetAccountNumberAndDateAfter(String targetAccount,
                                                                     LocalDateTime date);
  List<Transaction> findTransactionBySourceAccountNumberAndTargetAccountNumberAndDateAfter(
      String targetAccount,
      String sourceAccount,
      LocalDateTime date);

}
