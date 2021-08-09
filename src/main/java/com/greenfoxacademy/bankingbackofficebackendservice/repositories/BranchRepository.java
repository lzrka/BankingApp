package com.greenfoxacademy.bankingbackofficebackendservice.repositories;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends CrudRepository<Branch, Integer> {

  List<Branch> findAll();

}
