package com.greenfoxacademy.bankingbackofficebackendservice.repositories;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account,Integer> {

  List<Account> findAll();

  boolean existsByAccountNumber(String accountNumber);

  Optional<Account> findByAccountNumber(String accountNumber);

}
