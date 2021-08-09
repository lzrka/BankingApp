package com.greenfoxacademy.bankingbackofficebackendservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Employee;
import com.greenfoxacademy.bankingbackofficebackendservice.services.EmployeeService;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize(value = "hasRole('ROLE_Api.ADMIN')")
@RequestMapping("/api")
public class EmployeeController {

  @Autowired
  private EmployeeService employeeServiceImpl;

  @ApiOperation(value = "View a list of all the Employees")
  @GetMapping("/employees")
  public ResponseEntity<List<Employee>> getEmployees() {
    return new ResponseEntity<>(employeeServiceImpl.getAllEmployees(), HttpStatus.OK);
  }

  @ApiOperation(value = "Retrieve Employee with an ID")
  @GetMapping("/employees/{employee_id}")
  public ResponseEntity<Employee> getEmployeeById(@PathVariable(name = "employee_id") Integer id) {
    return new ResponseEntity<>(employeeServiceImpl.getEmployeeById(id), HttpStatus.OK);
  }

  @ApiOperation(value = "Delete Employee with an ID")
  @DeleteMapping("/employees/{employee_id}")
  public ResponseEntity<Employee> deleteEmployee(@PathVariable(name = "employee_id") Integer id) {
    employeeServiceImpl.removeEmployee(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ApiOperation(value = "Create a new Employee")
  @PostMapping("/employees")
  public ResponseEntity<Employee> createEmployee(@Valid @RequestBody Employee employee)
      throws URISyntaxException {
    Employee savedEmployee = employeeServiceImpl.createEmployee(employee);

    return ResponseEntity.created(new URI("/api/employees/" + savedEmployee.getId()))
        .body(savedEmployee);
  }

  @ApiOperation(value = "Update existing Employee with an ID")
  @PatchMapping(path = "/employees/{employee_id}", consumes = "application/json-patch+json")
  public ResponseEntity<Employee> updateEmployeeWithPatch(@RequestBody JsonPatch patch,
                                                          @PathVariable(name = "employee_id")
                                                              Integer id) {
    try {
      Employee employee = employeeServiceImpl.getEmployeeById(id);
      Employee employeePatched = applyPatchToEmployee(patch, employee);
      employeeServiceImpl.updateEmployee(employeePatched);
      return ResponseEntity.ok(employeePatched);
    } catch (JsonPatchException | JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private Employee applyPatchToEmployee(
      JsonPatch patch, Employee targetAccount) throws JsonPatchException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode patched =
        patch.apply(objectMapper.convertValue(targetAccount, JsonNode.class));
    return objectMapper.treeToValue(patched, Employee.class);
  }

}
