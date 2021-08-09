package com.greenfoxacademy.bankingbackofficebackendservice.apiTests;

import static com.greenfoxacademy.bankingbackofficebackendservice.apiTests.BranchAPITest.asJsonString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Employee;
import com.greenfoxacademy.bankingbackofficebackendservice.services.EmployeeService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class EmployeeAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  EmployeeService employeeService;

  @Test
  @DisplayName("Test GET '/api/employees' 200 OK")
  public void getEmployeesTest() throws Exception {
    Employee employee0 = Employee.builder().id(0).name("John Doe").branch(
        Branch.builder().id(0).address("address0").city("city0").zip("zip0").build()).build();
    Employee employee1 = Employee.builder().id(1).name("Caroline Hopkins").branch(
        Branch.builder().id(1).address("address1").city("city1").zip("zip1").build()).build();

    Mockito.when(employeeService.getAllEmployees())
        .thenReturn(Lists.newArrayList(employee0, employee1));

    mockMvc.perform(get("/api/employees").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(0)))
        .andExpect(jsonPath("$[0].name", is("John Doe")))
        .andExpect(jsonPath("$[0].branch.address", is("address0")))
        .andExpect(jsonPath("$[0].branch.city", is("city0")))
        .andExpect(jsonPath("$[0].branch.zip", is("zip0")))
        .andExpect(jsonPath("$[1].id", is(1)))
        .andExpect(jsonPath("$[1].name", is("Caroline Hopkins")))
        .andExpect(jsonPath("$[1].branch.address", is("address1")))
        .andExpect(jsonPath("$[1].branch.city", is("city1")))
        .andExpect(jsonPath("$[1].branch.zip", is("zip1")));

  }

  @Test
  @DisplayName("Test GET '/api/employees/0' 200 OK")
  public void getEmployeeByIdTest() throws Exception {
    Employee employee0 = Employee.builder().id(0).name("John Doe").branch(
        Branch.builder().id(0).address("address0").city("city0").zip("zip0").build()).build();

    Mockito.when(employeeService.getEmployeeById(employee0.getId()))
        .thenReturn(employee0);

    mockMvc.perform(get("/api/employees/" + employee0.getId().toString())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(0)))
        .andExpect(jsonPath("$.name", is("John Doe")))
        .andExpect(jsonPath("$.branch.address", is("address0")))
        .andExpect(jsonPath("$.branch.city", is("city0")))
        .andExpect(jsonPath("$.branch.zip", is("zip0")));
  }

  @Test
  @DisplayName("Test GET '/api/employees/0' 404 NOT FOUND")
  public void employeeNotFound() throws Exception {
    Employee employee0 = new Employee();
    employee0.setId(0);

    Mockito.when(employeeService.getEmployeeById(employee0.getId()))
        .thenThrow(
            new ResourceNotFoundException(
                "Employee with id: " + employee0.getId() + " doesn't exist"));

    mockMvc.perform(get("/api/employees/" + employee0.getId().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Employee with id: " + employee0.getId() + " doesn't exist")));
  }

  @Test
  @DisplayName("Test DELETE '/api/employees/0' 204 NO CONTENT")
  public void deleteEmployeeTest() throws Exception {
    Employee employee0 = Employee.builder().id(0).name("John Doe").branch(
        Branch.builder().id(0).address("address0").city("city0").zip("zip0").build()).build();

    Mockito.doNothing().when(employeeService).removeEmployee(employee0.getId());

    mockMvc.perform(delete("/api/employees/" + employee0.getId()))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());

  }

  @Test
  @DisplayName("Test DELETE '/api/employee/0' 404 NOT FOUND")
  public void deletesEmployeeFails() throws Exception {
    Employee employee0 = new Employee();
    employee0.setId(0);

    Mockito.doThrow(new ResourceNotFoundException(
        "Employee with id: " + employee0.getId() + " doesn't exist"))
        .when(employeeService).removeEmployee(employee0.getId());

    mockMvc.perform(delete("/api/employees/" + employee0.getId()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Employee with id: " + employee0.getId() + " doesn't exist")));
  }

  @Test
  @DisplayName("Test POST '/api/employees' 201 CREATED")
  public void createEmployeeTest() throws Exception {
    Employee employee0 = Employee.builder().id(0).name("John Doe").branch(
        Branch.builder().id(0).address("address0").city("city0").zip("zip0").build()).build();

    Mockito.when(employeeService.createEmployee(Mockito.any(Employee.class))).thenReturn(employee0);

    mockMvc.perform(post("/api/employees")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(employee0)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, "/api/employees/0"))
        .andExpect(jsonPath("$.id", is(0)))
        .andExpect(jsonPath("$.name", is("John Doe")))
        .andExpect(jsonPath("$.branch.address", is("address0")))
        .andExpect(jsonPath("$.branch.city", is("city0")))
        .andExpect(jsonPath("$.branch.zip", is("zip0")));
  }

  @Test
  @DisplayName("TEST POST '/api/employees' 409 CONFLICT")
  public void conflictWhenEmployeeExist() throws Exception {
    Employee employee0 = Employee.builder().id(0).name("John Doe").branch(
        Branch.builder().id(0).address("address0").city("city0").zip("zip0").build()).build();

    Mockito.when(employeeService.createEmployee(Mockito.any(Employee.class)))
        .thenThrow(new ResourceAlreadyExistsException(
            "Employee with id: " + employee0.getId() + " already exists"));

    mockMvc.perform(post("/api/employees")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON)
        .content(asJsonString(employee0)))
        .andExpect(status().isConflict())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Employee with id: " + employee0.getId() + " already exists")));
  }

  @Test
  @DisplayName("Test PATCH '/api/employees/0' 204 NO CONTENT")
  public void updateEmployeeWithPatchTest() throws Exception {
    Employee employee0 = new Employee();
    employee0.setId(0);

    Mockito.when(employeeService.updateEmployee(employee0)).thenReturn(employee0);

    mockMvc.perform(patch("/api/employees/" + employee0.getId().toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(employee0)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$").doesNotExist());

  }
}
