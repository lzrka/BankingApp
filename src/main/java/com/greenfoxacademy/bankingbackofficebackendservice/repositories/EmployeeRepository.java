package com.greenfoxacademy.bankingbackofficebackendservice.repositories;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Employee;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

  List<Employee> findAll();
}
