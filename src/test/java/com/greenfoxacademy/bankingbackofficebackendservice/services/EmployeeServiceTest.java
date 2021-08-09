package com.greenfoxacademy.bankingbackofficebackendservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Employee;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.EmployeeRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class EmployeeServiceTest {

  @Autowired
  private EmployeeService employeeService;

  @MockBean
  private EmployeeRepository employeeRepository;

  @Test
  public void getAllEmployeesTestSuccess() {
    Branch branch1 =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    Branch branch2 =
        Branch.builder().id(2).address("Address 2.").city("City 2.").zip("2234").build();

    List<Employee> employees = new ArrayList<>();

    Employee employee1 = Employee.builder().id(1).name("name1").branch(branch1).build();
    Employee employee2 = Employee.builder().id(2).name("name2").branch(branch1).build();
    Employee employee3 = Employee.builder().id(3).name("name3").branch(branch2).build();

    employees.add(employee1);
    employees.add(employee2);
    employees.add(employee3);

    when(employeeRepository.findAll()).thenReturn(employees);

    List<Employee> expectedList = employeeService.getAllEmployees();

    assertEquals(3, expectedList.size());
    assertEquals(expectedList, employees);
    verify(employeeRepository, times(1)).findAll();
  }

  @Test
  public void getEmployeeByIdTestSuccess() {
    Branch branch1 =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    Employee employee1 = Employee.builder().id(11).name("name1").branch(branch1).build();

    when(employeeRepository.findById(11)).thenReturn(Optional.of(employee1));

    Employee employee = employeeService.getEmployeeById(11);

    assertEquals("name1", employee.getName());
    assertEquals(branch1, employee.getBranch());

  }

  @Test
  public void getEmployeeById() {
    Exception exception = Assertions.assertThrows(
        ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1));
    assertEquals("Employee with id: 1 doesn't exist", exception.getMessage());
  }

  @Test
  public void createEmployeeTestSuccess() {
    Branch branch1 =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    Employee employee1 = Employee.builder().id(11).name("name1").branch(branch1).build();

    when(employeeRepository.save(employee1)).thenReturn(employee1);

    Employee employee = employeeService.createEmployee(employee1);

    assertEquals("name1", employee.getName());
    assertEquals(branch1, employee.getBranch());
  }

  @Test
  public void createEmployeeTestAlreadyExists() {
    Branch branch1 =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    Employee employee1 = Employee.builder().id(11).name("name1").branch(branch1).build();

    when(employeeRepository.existsById(employee1.getId())).thenReturn(true);

    Exception exception = Assertions.assertThrows(
        ResourceAlreadyExistsException.class, () -> employeeService.createEmployee(employee1));
    assertEquals("Employee with id: 11 already exist", exception.getMessage());
  }

  @Test
  public void updateEmployeeTestSuccess() {
    Branch branch1 =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    Employee employee1 = Employee.builder().id(11).name("name1").branch(branch1).build();

    when(employeeRepository.existsById(employee1.getId())).thenReturn(true);
    when(employeeRepository.save(employee1)).thenReturn(employee1);

    Employee updateEmployee = employeeService.updateEmployee(employee1);
    assertEquals(updateEmployee, employee1);
  }

  @Test
  public void updateEmployeeTestDoesntExist() {
    Branch branch1 =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    Employee employee1 = Employee.builder().id(11).name("name1").branch(branch1).build();

    when(employeeRepository.existsById(employee1.getId())).thenReturn(false);

    Exception exception = Assertions.assertThrows(
        ResourceNotFoundException.class, () -> employeeService.updateEmployee(employee1));
    assertEquals("Employee with id: 11 doesn't exist", exception.getMessage());
  }

  @Test
  public void removeEmployeeTestSuccess() {
    when(employeeRepository.existsById(1)).thenReturn(true);
    employeeService.removeEmployee(1);
    verify(employeeRepository, times(1)).deleteById(1);
  }

  @Test
  public void removeEmployeeTestDoesntExist() {
    when(employeeRepository.existsById(1)).thenReturn(false);
    Exception exception = assertThrows(
        ResourceNotFoundException.class, () -> employeeService.removeEmployee(1));
    assertEquals("Employee with id: 1 doesn't exist", exception.getMessage());
  }
}
