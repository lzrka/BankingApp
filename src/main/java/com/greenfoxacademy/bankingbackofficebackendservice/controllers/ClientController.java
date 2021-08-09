package com.greenfoxacademy.bankingbackofficebackendservice.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import com.greenfoxacademy.bankingbackofficebackendservice.services.ClientService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize(value = "hasRole('ROLE_Api.USER')")
@RequestMapping("/api")
public class ClientController {

  @Autowired
  private ClientService clientService;

  @ApiOperation(value = "View a list of all the clients or search by clientId", notes = "Search clients by clientID using the 'q' query parameter")
  @GetMapping("/clients")
  public ResponseEntity<List<Client>> clients(
      @RequestParam(name = "q", required = false) String query) {

    if (query != null) {
      return new ResponseEntity<>(clientService.findByClientId(query), HttpStatus.OK);
    }

    return new ResponseEntity<>(clientService.getAllClients(), HttpStatus.OK);
  }

  @ApiOperation(value = "Retrieve a client with an ID")
  @GetMapping("/clients/{id}")
  public ResponseEntity<Client> clientById(@PathVariable Integer id) {
    return new ResponseEntity<>(clientService.getClientById(id), HttpStatus.OK);
  }

  @ApiOperation(value = "Delete a client with an ID")
  @DeleteMapping("/clients/{id}")
  public ResponseEntity<Void> deleteClient(@PathVariable Integer id) {
    clientService.removeClient(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ApiOperation(value = "Add a new client")
  @PostMapping("/clients")
  public ResponseEntity<Client> createClient(@Valid @RequestBody Client client)
      throws URISyntaxException {
    Client savedClient = clientService.createClient(client);

    return ResponseEntity.created(new URI("/api/clients/" + savedClient.getId()))
        .body(savedClient);
  }

  @PatchMapping(path = "/clients/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<Client> updateClientWithPatch(@RequestBody JsonPatch patch,
                                                      @PathVariable Integer id) {
    try {
      Client client = clientService.getClientById(id);
      Client patchedClient = applyPatchToClient(patch, client);
      clientService.updateClient(patchedClient);
      return ResponseEntity.ok(patchedClient);
    } catch (JsonPatchException | JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private Client applyPatchToClient(
      JsonPatch patch, Client client) throws JsonPatchException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    JsonNode patched =
        patch.apply(objectMapper.convertValue(client, JsonNode.class));
    return objectMapper.treeToValue(patched, Client.class);
  }
}

