package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.ClientRepository;
import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

  @Autowired
  ClientRepository clientRepository;

  public List<Client> getAllClients() {
    return clientRepository.findAll();
  }

  public Client getClientById(Integer id) {
    Objects.requireNonNull(id);
    return clientRepository.findById(id)
        .orElseThrow(
            () -> new ResourceNotFoundException("Client with id: " + id + " doesn't exist"));

  }

  public List<Client> findByClientId(String query) {
    return clientRepository.findByClientIDContaining(query);
  }

  @Transactional
  public Client createClient(Client client) {
    Objects.requireNonNull(client);
    if (client.getId() != null && existsById(client.getId())) {
      throw new ResourceAlreadyExistsException(
          "Client with id: " + client.getId() + " already exists");
    } else {
      return clientRepository.save(client);
    }
  }

  @Transactional
  public Client updateClient(Client client) {
    Objects.requireNonNull(client);
    if (!existsById(client.getId())) {
      throw new ResourceNotFoundException("Client with id: " + client.getId() + " doesn't exist");
    } else {
      return clientRepository.save(client);
    }
  }

  @Transactional
  public void removeClient(Integer id) {
    Objects.requireNonNull(id);
    if (!existsById(id)) {
      throw new ResourceNotFoundException("Client with id: " + id + " doesn't exist ");
    }
    clientRepository.deleteById(id);
  }

  private boolean existsById(Integer id) {
    return clientRepository.existsById(id);
  }
}
