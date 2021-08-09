package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Employee;
import java.util.List;

public interface EmployeeService {

  List<Employee> getAllEmployees();

  Employee getEmployeeById(Integer id);

  Employee createEmployee(Employee employee);

  Employee updateEmployee(Employee employee);

  void removeEmployee(Integer id);
}
