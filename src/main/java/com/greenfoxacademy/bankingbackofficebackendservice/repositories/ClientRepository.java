package com.greenfoxacademy.bankingbackofficebackendservice.repositories;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Integer> {

  List<Client> findAll();
  List<Client> findByClientIDContaining(String query);
}
