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

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import com.greenfoxacademy.bankingbackofficebackendservice.services.ClientService;
import java.time.LocalDate;
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
public class ClientAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private ClientService clientService;

  @Test
  @DisplayName("Test GET '/api/clients' 200 OK")
  public void getsAllClients() throws Exception {

    Client client1 = Client.builder().id(0).name("name0").email("name0@email.com").phone("+36301234567").address("address0")
        .birthDate(LocalDate.of(2020, 1, 1)).pin("1234").build();
    Client client2 = Client.builder().id(1).name("name1").email("name1@email.com").phone("+36303456789").address("address1")
        .birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.when(clientService.getAllClients()).thenReturn(Lists.newArrayList(client1, client2));

    mockMvc.perform(get("/api/clients").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(0)))
        .andExpect(jsonPath("$[0].name", is("name0")))
        .andExpect(jsonPath("$[0].email", is("name0@email.com")))
        .andExpect(jsonPath("$[0].phone", is("+36301234567")))
        .andExpect(jsonPath("$[0].address", is("address0")))
        .andExpect(jsonPath("$[0].birthDate", is("2020-01-01")))
        .andExpect(jsonPath("$[0].pin", is("1234")))
        .andExpect(jsonPath("$[1].id", is(1)))
        .andExpect(jsonPath("$[1].name", is("name1")))
        .andExpect(jsonPath("$[1].email", is("name1@email.com")))
        .andExpect(jsonPath("$[1].phone", is("+36303456789")))
        .andExpect(jsonPath("$[1].address", is("address1")))
        .andExpect(jsonPath("$[1].birthDate", is("2021-02-02")))
        .andExpect(jsonPath("$[1].pin", is("5678")));
  }

  @Test
  @DisplayName("Test POST '/api/clients' 201 CREATED")
  public void createsNewClient() throws Exception {
    Client clientToReturn = Client.builder().id(1).name("name1").email("name1@email.com").phone("+36303456789").address("address1").birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.when(clientService.createClient(Mockito.any(Client.class))).thenReturn(clientToReturn);

    mockMvc.perform(post("/api/clients")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"id\": \"1\", \"name\": \"name1\", \"email\": \"name1@email.com\", \"phone\": \"+36303456789\", \"address\": \"address1\", \"birthDate\": \"2021-02-02\", \"pin\": 5678}"))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(header().string(HttpHeaders.LOCATION, "/api/clients/1"))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("name1")))
        .andExpect(jsonPath("$.email", is("name1@email.com")))
        .andExpect(jsonPath("$.phone", is("+36303456789")))
        .andExpect(jsonPath("$.address", is("address1")))
        .andExpect(jsonPath("$.birthDate", is("2021-02-02")))
        .andExpect(jsonPath("$.pin", is("5678")));
  }

  @Test
  @DisplayName("Test POST '/api/clients' 409 CONFLICT")
  public void conflictWhenClientExist() throws Exception {
    Client clientToPost = Client.builder().id(2).name("name1").email("name1@email.com").phone("+36303456789").address("address1")
        .birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.when(clientService.createClient(Mockito.any(Client.class)))
        .thenThrow(new ResourceAlreadyExistsException(
            "Client with id: " + clientToPost.getId() + " already exists"));

    mockMvc.perform(post("/api/clients")
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .accept(MediaType.APPLICATION_JSON)
        .content("{\"id\": \"1\", \"name\": \"name1\", \"email\": \"name1@email.com\", \"phone\": \"+36303456789\", \"address\": \"address1\", \"birthDate\": \"2021-02-02\", \"pin\": 5678}"))
        .andExpect(status().isConflict())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Client with id: " + clientToPost.getId() + " already exists")));
  }

  @Test
  @DisplayName("Test GET '/api/clients/1' 200 OK")
  public void getsClientById() throws Exception {
    Client client = Client.builder().id(1).name("name1").email("name1@email.com").phone("+36303456789").address("address1")
        .birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.when(clientService.getClientById(client.getId())).thenReturn(client);

    mockMvc.perform(get("/api/clients/" + client.getId().toString())
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(1)))
        .andExpect(jsonPath("$.name", is("name1")))
        .andExpect(jsonPath("$.email", is("name1@email.com")))
        .andExpect(jsonPath("$.phone", is("+36303456789")))
        .andExpect(jsonPath("$.address", is("address1")))
        .andExpect(jsonPath("$.birthDate", is("2021-02-02")))
        .andExpect(jsonPath("$.pin", is("5678")));
  }

  @Test
  @DisplayName("Test GET '/api/clients/1' 404 NOT FOUND")
  public void clientNotFound() throws Exception {
    Client client = Client.builder().id(1).build();

    Mockito.when(clientService.getClientById(client.getId()))
        .thenThrow(
            new ResourceNotFoundException("Client with id: " + client.getId() + " doesn't exists"));

    mockMvc.perform(get("/api/clients/" + client.getId().toString()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Client with id: " + client.getId() + " doesn't exists")));
  }

  @Test
  @DisplayName("Test PUT '/api/clients/1' 204 NO CONTENT")
  public void clientGetsUpdated() throws Exception {
    Client clientToUpdate = Client.builder().id(1).name("name1").email("name1@email.com").phone("+36303456789").address("address1")
        .birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.when(clientService.updateClient(clientToUpdate)).thenReturn(clientToUpdate);

    mockMvc.perform(put("/api/clients/" + clientToUpdate.getId().toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"id\": \"1\", \"name\": \"name1\", \"email\": \"name1@email.com\", \"phone\": \"+36303456789\", \"address\": \"address1\", \"birthDate\": \"2021-02-02\", \"pin\": 5678}"))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("Test PUT '/api/clients/1' 404 NOT FOUND")
  public void clientUpdateFails() throws Exception {
    Client clientToUpdate = Client.builder().id(1).name("name1").email("name1@email.com").phone("+36303456789").address("address1")
        .birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.doThrow(new ResourceNotFoundException(
        "Client with id: " + clientToUpdate.getId() + " doesn't exists"))
        .when(clientService).updateClient(Mockito.any(Client.class));

    mockMvc.perform(put("/api/clients/" + clientToUpdate.getId().toString())
        .contentType(MediaType.APPLICATION_JSON_VALUE)
        .content("{\"id\": \"1\", \"name\": \"name1\", \"email\": \"name1@email.com\", \"phone\": \"+36303456789\", \"address\": \"address1\", \"birthDate\": \"2021-02-02\", \"pin\": 5678}"))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Client with id: " + clientToUpdate.getId() + " doesn't exists")));
  }

  @Test
  @DisplayName("Test DELETE '/api/clients/1' 204 NO CONTENT")
  public void deletesClient() throws Exception {
    Client clientToDelete = Client.builder().id(1).name("name1").email("name1@email.com").phone("+36303456789").address("address1")
        .birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.doNothing().when(clientService).removeClient(clientToDelete.getId());

    mockMvc.perform(delete("/api/clients/" + clientToDelete.getId()))
        .andExpect(status().isNoContent())
        .andExpect(jsonPath("$").doesNotExist());
  }

  @Test
  @DisplayName("Test DELETE '/api/clients/1' 404 NOT FOUND")
  public void deletesClientFails() throws Exception {
    Client clientToDelete = Client.builder().id(1).name("name1").email("name1@email.com").phone("+36303456789").address("address1")
        .birthDate(LocalDate.of(2021, 2, 2)).pin("5678").build();

    Mockito.doThrow(new ResourceNotFoundException(
        "Client with id: " + clientToDelete.getId() + " doesn't exists"))
        .when(clientService).removeClient(clientToDelete.getId());

    mockMvc.perform(delete("/api/clients/" + clientToDelete.getId()))
        .andExpect(status().isNotFound())
        .andExpect(content().string(org.hamcrest.Matchers
            .containsString("Client with id: " + clientToDelete.getId() + " doesn't exists")));
  }
}
