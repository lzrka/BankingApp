package com.greenfoxacademy.bankingbackofficebackendservice.apiTests;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import com.greenfoxacademy.bankingbackofficebackendservice.services.BranchService;
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
public class BranchAPITest {



  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BranchService branchService;

  @Test
  @DisplayName("Test GET '/api/branches' 200 OK")
  public void getsAllBranches() throws Exception {

    Branch branch1 = Branch.builder().id(0).address("address0").city("city0").zip("zip0").build();
    Branch branch2 = Branch.builder().id(1).address("address1").city("city1").zip("zip1").build();

    Mockito.when(branchService.getAllBranches()).thenReturn(Lists.newArrayList(branch1, branch2));

    mockMvc.perform(get("/api/branches").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(0)))
        .andExpect(jsonPath("$[0].address", is("address0")))
        .andExpect(jsonPath("$[0].city", is("city0")))
        .andExpect(jsonPath("$[0].zip", is("zip0")))
        .andExpect(jsonPath("$[1].id", is(1)))
        .andExpect(jsonPath("$[1].address", is("address1")))
        .andExpect(jsonPath("$[1].city", is("city1")))
        .andExpect(jsonPath("$[1].zip", is("zip1")));
  }

  @Test
  @DisplayName("Test POST '/api/branches' 201 CREATED")
  public void createsNewBranch() throws Exception {
    Branch branchToPost = Branch.builder().address("address1").city("city1").zip("zip1").build();
    Branch branchToReturn =
        Branch.builder().id(1).address("address1").city("city1").zip("zip1").build();

    Mockito.when(branchService.createBranch(Mockito.any(Branch.class))).thenReturn(branchToReturn);

    mockMvc.perform(post("/api/branches")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(branchToPost)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, "/api/branches/1"))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.address", is("address1")))
        .andExpect(jsonPath("$.city", is("city1")))
        .andExpect(jsonPath("$.zip", is("zip1")));
  }

  @Test
  @DisplayName("TEST POST '/api/branches' 409 CONFLICT")
  public void conflictWhenBranchExist() throws Exception {
    Branch branchToPost =
        Branch.builder().id(2).address("address2").city("city2").zip("zip2").build();

    Mockito.when(branchService.createBranch(Mockito.any(Branch.class)))
        .thenThrow(new ResourceAlreadyExistsException(
            "Branch with id: " + branchToPost.getId() + " already exists"));

    mockMvc.perform(post("/api/branches")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON)
        .content(asJsonString(branchToPost)))
        .andExpect(status().isConflict())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Branch with id: " + branchToPost.getId() + " already exists")));


  }

  @Test
  @DisplayName("Test GET '/api/branches/1' 200 OK")
  public void getsBranchById() throws Exception {
    Branch branch = Branch.builder().id(1).address("address0").city("city0").zip("zip0").build();

    Mockito.when(branchService.getBranchById(branch.getId())).thenReturn(branch);

    mockMvc.perform(get("/api/branches/" + branch.getId().toString())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.address", is("address0")))
        .andExpect(jsonPath("$.city", is("city0")))
        .andExpect(jsonPath("$.zip", is("zip0")));
  }

  @Test
  @DisplayName("Test GET '/api/branches/1' 404 NOT FOUND")
  public void branchNotFound() throws Exception {
    Branch branch = Branch.builder().id(1).build();

    Mockito.when(branchService.getBranchById(branch.getId()))
        .thenThrow(
            new ResourceNotFoundException("Branch with id: " + branch.getId() + " doesn't exists"));

    mockMvc.perform(get("/api/branches/" + branch.getId().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Branch with id: " + branch.getId() + " doesn't exists")));
  }

  @Test
  @DisplayName("Test PUT '/api/branches/1' 204 NO CONTENT")
  public void branchGetsUpdated() throws Exception {
    Branch branchToUpdate =
        Branch.builder().id(1).address("address2").city("city2").zip("zip2").build();

    Mockito.when(branchService.updateBranch(branchToUpdate)).thenReturn(branchToUpdate);

    mockMvc.perform(put("/api/branches/" + branchToUpdate.getId().toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(branchToUpdate)))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("Test PUT '/api/branches/1' 404 NOT FOUND")
  public void branchUpdateFails() throws Exception {
    Branch branchToUpdate =
        Branch.builder().id(1).address("address1").city("city2").zip("zip2").build();

    Mockito.doThrow(new ResourceNotFoundException(
        "Branch with id: " + branchToUpdate.getId() + " doesn't exists"))
        .when(branchService).updateBranch(Mockito.any(Branch.class));

    mockMvc.perform(put("/api/branches/" + branchToUpdate.getId().toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(branchToUpdate)))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Branch with id: " + branchToUpdate.getId() + " doesn't exists")));
  }

  @Test
  @DisplayName("Test DELETE '/api/branches/1' 204 NO CONTENT")
  public void deletesBranch() throws Exception {
    Branch branchToDelete =
        Branch.builder().id(1).address("address0").city("city0").zip("zip0").build();

    Mockito.doNothing().when(branchService).removeBranch(branchToDelete.getId());

    mockMvc.perform(delete("/api/branches/" + branchToDelete.getId()))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("Test DELETE '/api/branches/1' 404 NOT FOUND")
  public void deletesBranchFails() throws Exception {
    Branch branchToDelete =
        Branch.builder().id(1).address("address0").city("city0").zip("zip0").build();

    Mockito.doThrow(new ResourceNotFoundException(
        "Branch with id: " + branchToDelete.getId() + " doesn't exists"))
        .when(branchService).removeBranch(branchToDelete.getId());

    mockMvc.perform(delete("/api/branches/" + branchToDelete.getId()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Branch with id: " + branchToDelete.getId() + " doesn't exists")));
  }

  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
