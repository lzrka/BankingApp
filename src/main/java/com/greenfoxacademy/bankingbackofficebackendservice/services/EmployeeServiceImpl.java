package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Employee;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.EmployeeRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService{

  @Autowired
  EmployeeRepository employeeRepository;

  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  public Employee getEmployeeById(Integer id) {
    Objects.requireNonNull(id);
    return employeeRepository.findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Employee with id: " + id + " doesn't exist"));
  }

  @Transactional
  public Employee createEmployee(Employee employee) {
    Objects.requireNonNull(employee);
    if (employee.getId() != null && existsById(employee.getId())) {
      throw new ResourceAlreadyExistsException(
          "Employee with id: " + employee.getId() + " already exist");
    } else {
      return employeeRepository.save(employee);
    }

  }

  @Transactional
  public Employee updateEmployee(Employee employee) {
    Objects.requireNonNull(employee);
    if (!existsById(employee.getId())) {
      throw new ResourceNotFoundException(
          "Employee with id: " + employee.getId() + " doesn't exist");
    } else {
      return employeeRepository.save(employee);
    }
  }

  @Transactional
  public void removeEmployee(Integer id) {
    Objects.requireNonNull(id);
    if (!existsById(id)) {
      throw new ResourceNotFoundException("Employee with id: " + id + " doesn't exist");
    }
    employeeRepository.deleteById(id);
  }

  private boolean existsById(Integer id) {
    return employeeRepository.existsById(id);
  }
}
