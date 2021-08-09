package com.greenfoxacademy.bankingbackofficebackendservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.ClientRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class ClientServiceTest {
  @Autowired
   private ClientService service;

  @MockBean
  private ClientRepository repository;

  @Before
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void getAllClientsTest() {
    List<Client> testList = new ArrayList<>();

    Client client0 = Client.builder()
        .address("address0")
        .name("Attila")
        .email("A@0.com")
        .phone("0")
        .birthDate(LocalDate.of(2000, 11, 11))
        .pin("000").build();

    Client client1 = Client.builder()
        .address("address1")
        .name("Atilla")
        .email("A@1.com")
        .phone("1")
        .birthDate(LocalDate.of(2001, 11, 11))
        .pin("111").build();

    testList.add(client0);

    testList.add(client1);

    when(repository.findAll()).thenReturn(testList);

    List<Client> expect = service.getAllClients();

    assertEquals(2, expect.size());
    assertEquals(expect, testList);
    verify(repository, times(1)).findAll();
  }

  @Test
  public void getClientById() {
    Client client0 = Client.builder()
        .address("address0")
        .name("Attila")
        .email("A@0.com")
        .phone("0")
        .birthDate(LocalDate.of(2000, 11, 11))
        .pin("000").build();

    when(repository.findById(1)).thenReturn(Optional.of(client0));

    Client client = service.getClientById(1);

    assertEquals("Attila", client.getName());
    assertEquals("A@0.com", client.getEmail());
    assertEquals("0", client.getPhone());
    assertEquals("address0", client.getAddress());
    assertEquals("000", client.getPin());
  }

  @Test
  public void testClientWithIdDoesntExist() {
    Exception exception =
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.getClientById(1));
    assertEquals("Client with id: 1 doesn't exist", exception.getMessage());
  }

  @Test
  public void testGetByClientId() {
    List<Client> testList = new ArrayList<>();

    Client client0 = Client.builder()
        .address("address0")
        .name("Attila")
        .email("A@0.com")
        .phone("0")
        .birthDate(LocalDate.of(2000, 11, 11))
        .pin("000").build();

    Client client1 = Client.builder()
        .address("address1")
        .name("Atilla")
        .email("A@1.com")
        .phone("1")
        .birthDate(LocalDate.of(2001, 11, 11))
        .pin("111").build();

    testList.add(client0);

    testList.add(client1);

    when(repository.findByClientIDContaining("")).thenReturn(testList);

    List<Client> expect = service.findByClientId("");

    assertEquals(2, expect.size());
    assertEquals(expect, testList);
    verify(repository, times(1)).findByClientIDContaining("");
  }

  @Test
  public void testCreateClientSuccess() {
    Client client0 = Client.builder()
        .address("address0")
        .name("Attila")
        .email("A@0.com")
        .phone("0")
        .birthDate(LocalDate.of(2000, 11, 11))
        .pin("000").build();

    when(repository.save(client0)).thenReturn(client0);

    Client client = service.createClient(client0);

    assertEquals("Attila", client.getName());
    assertEquals("A@0.com", client.getEmail());
    assertEquals("0", client.getPhone());
    assertEquals("address0", client.getAddress());
    assertEquals("000", client.getPin());
  }

  @Test()
  public void testCreateClientWhenClientAlreadyExists() {
    Client client0 = Client.builder()
        .id(0)
        .address("address0")
        .name("Attila")
        .email("A@0.com")
        .phone("0")
        .birthDate(LocalDate.of(2000, 11, 11))
        .pin("000").build();

    when(repository.existsById(client0.getId())).thenReturn(true);

    Exception exception = Assertions
        .assertThrows(ResourceAlreadyExistsException.class, () -> service.createClient(client0));

    assertEquals("Client with id: 0 already exists", exception.getMessage());
  }

  @Test
  public void TestUpdateClientSuccess() {
    Client client0 = Client.builder()
        .id(0)
        .address("address0")
        .name("Attila")
        .email("A@0.com")
        .phone("0")
        .birthDate(LocalDate.of(2000, 11, 11))
        .pin("000").build();

    when(repository.existsById(client0.getId())).thenReturn(true);
    when(repository.save(client0)).thenReturn(client0);

    Client updateClient = service.updateClient(client0);
    assertEquals(updateClient, client0);
  }

  @Test
  public void clientDoesntExistCantUpdate() {
    Client client0 = Client.builder()
        .address("address0")
        .name("Attila")
        .email("A@0.com")
        .phone("0")
        .birthDate(LocalDate.of(2000, 11, 11))
        .pin("000").build();

    when(repository.existsById(client0.getId())).thenReturn(false);

    Exception exception = Assertions
        .assertThrows(ResourceNotFoundException.class, () -> service.updateClient(client0));

    assertEquals("Client with id: null doesn't exist", exception.getMessage());
  }

  @Test
  public void testDelete() {
    when(repository.existsById(1)).thenReturn(true);
    service.removeClient(1);
    verify(repository, times(1)).deleteById(1);
  }

  @Test
  public void testCantDeleteDoesntExist() {
    when(repository.existsById(1)).thenReturn(false);
    Exception exception =
        Assertions.assertThrows(ResourceNotFoundException.class, () -> service.removeClient(1));
    assertEquals("Client with id: 1 doesn't exist", exception.getMessage());
  }
}
