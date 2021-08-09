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
import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.services.AccountServiceImpl;
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
public class AccountAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private AccountServiceImpl accountService;

  @Test
  @DisplayName("Test GET 'api/accounts' is ok")
  public void getsAllAccounts() throws Exception {
    Account account0 = new Account();
    account0.setId(0);
    Account account1 = new Account();
    account1.setId(1);

    Mockito.when(accountService.getAllAccounts())
        .thenReturn(Lists.newArrayList(account0, account1));

    mockMvc.perform(get("/api/accounts").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(0)))
        .andExpect(jsonPath("$[0].currency", is("HUF")))
        .andExpect(jsonPath("$[1].id", is(1)))
        .andExpect(jsonPath("$[1].currency", is("HUF")));
  }

  @Test
  @DisplayName("Test POST 'api/accounts' 201 CREATED")
  public void createNewAccountsIsCreated() throws Exception {
    Account accountPost = new Account();
    accountPost.setId(1);
    Account accountReturn = new Account();
    accountReturn.setId(1);

    Mockito.when(accountService.createAccount(Mockito.any(Account.class)))
        .thenReturn(accountReturn);

    mockMvc.perform(post("/api/accounts")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(accountPost)))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, "/api/accounts/1"))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.currency", is("HUF")))
        .andExpect(jsonPath("$.value", is(1000)));
  }

  @Test
  @DisplayName("TEST POST '/api/branches' 409 CONFLICT")
  public void conflictWhenAccountExist() throws Exception {
    Account accountPost = new Account();
    accountPost.setId(1);

    Mockito.when(accountService.createAccount(Mockito.any(Account.class)))
        .thenThrow(new ResourceAlreadyExistsException(
            "Account with id: " + accountPost.getId() + " already exists"));

    mockMvc.perform(post("/api/accounts")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON)
        .content(asJsonString(accountPost)))
        .andExpect(status().isConflict())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Account with id: " + accountPost.getId() + " already exists")));

  }

  @Test
  @DisplayName("Test GET '/api/accounts/1' 200 OK")
  public void getsAccountById() throws Exception {
    Account account = new Account();
    account.setId(1);

    Mockito.when(accountService.getAccountById(account.getId())).thenReturn(account);

    mockMvc.perform(get("/api/accounts/" + account.getId().toString())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.currency", is("HUF")))
        .andExpect(jsonPath("$.value", is(1000)));
  }
  @Test
  @DisplayName("Test GET '/api/accounts/1' 404 NOT FOUND")
  public void accountNotFound() throws Exception {
    Account account = new Account();
    account.setId(1);

    Mockito.when(accountService.getAccountById(account.getId()))
        .thenThrow(
            new ResourceNotFoundException("Account with id: " + account.getId() + " doesn't exist"));

    mockMvc.perform(get("/api/accounts/" + account.getId().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Account with id: " + account.getId() + " doesn't exist")));
  }

  @Test
  @DisplayName("Test PATCH '/api/accounts/1' 204 NO CONTENT")
  public void accountGetsUpdated() throws Exception {
    Account account = new Account();
    account.setId(1);

    Mockito.when(accountService.updateAccount(account)).thenReturn(account);

    mockMvc.perform(patch("/api/accounts/" + account.getId().toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content(asJsonString(account)))
        .andExpect(status().isUnsupportedMediaType())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("Test DELETE '/api/accounts/1' 204 NO CONTENT")
  public void deletesAccount() throws Exception {
    Account account = new Account();
    account.setId(1);

    Mockito.doNothing().when(accountService).removeAccount(account.getId());

    mockMvc.perform(delete("/api/accounts/" + account.getId()))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("Test DELETE '/api/accounts/1' 404 NOT FOUND")
  public void deletesAccountFails() throws Exception {
    Account account = new Account();
    account.setId(1);

    Mockito.doThrow(new ResourceNotFoundException(
        "Account with id: " + account.getId() + " doesn't exist"))
        .when(accountService).removeAccount(account.getId());

    mockMvc.perform(delete("/api/accounts/" + account.getId()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Account with id: " + account.getId() + " doesn't exist")));
  }
}
