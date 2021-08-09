package com.greenfoxacademy.bankingbackofficebackendservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.models.dtos.IbanTransferDTO;
import com.greenfoxacademy.bankingbackofficebackendservice.models.dtos.TransferDTO;
import com.greenfoxacademy.bankingbackofficebackendservice.services.AccountService;
import com.greenfoxacademy.bankingbackofficebackendservice.services.TransferService;
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

@PreAuthorize(value = "hasRole('ROLE_Api.USER')")
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

  @Autowired
  private AccountService accountService;

  @Autowired
  private TransferService transferService;

  @ApiOperation("View all accounts")
  @GetMapping("")
  public ResponseEntity<List<Account>> getAllAccounts() {
    return new ResponseEntity<>(accountService.getAllAccounts(), HttpStatus.OK);
  }

  @ApiOperation(value = "Retrieve an account with an ID")
  @GetMapping("/{id}")
  public ResponseEntity<Account> getAccountById(@PathVariable Integer id) {
    return new ResponseEntity<>(accountService.getAccountById(id), HttpStatus.OK);
  }

  @ApiOperation(value = "Delete an account with an ID")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteAccount(@PathVariable Integer id) {
    accountService.removeAccount(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ApiOperation(value = "Add a new account")
  @PostMapping("")
  public ResponseEntity<Account> createAccount(@Valid @RequestBody Account account)
      throws URISyntaxException {
    Account savedAccount = accountService.createAccount(account);

    return ResponseEntity.created(new URI("/api/accounts/" +
        savedAccount.getId()))
        .body(savedAccount);
  }

  @ApiOperation(value = "Update existing account with an ID")
  @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<Account> updateAccountWithPatch(@RequestBody JsonPatch patch,
                                                        @PathVariable Integer id) {
    try {
      Account account = accountService.getAccountById(id);
      Account accountPatched = applyPatchToAccount(patch, account);
      accountService.updateAccount(accountPatched);
      return ResponseEntity.ok(accountPatched);
    } catch (JsonPatchException | JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @ApiOperation(value = "Transfer money between two accounts internally")
  @PostMapping("/{id}/internal-transaction")
  public ResponseEntity<Void> createInternalTransaction(@PathVariable Integer id, @RequestBody
  @Valid TransferDTO transferDTO) {
    transferService.transferMoneyWithinTheBank(id, transferDTO.getTargetAccountNumber(),
        transferDTO.getAmount());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ApiOperation(value = "Transfer money between two accounts externally")
  @PostMapping("/{id}/external-transaction")
  public ResponseEntity<Void> createExternalTransaction(@PathVariable Integer id, @RequestBody
  @Valid IbanTransferDTO ibanTransferDTO) {
    transferService.transferMoneyToAnotherBank(id, ibanTransferDTO.getTargetAccountIban(),
        ibanTransferDTO.getAmount(), ibanTransferDTO.getCurrency());
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private Account applyPatchToAccount(
      JsonPatch patch, Account targetAccount) throws JsonPatchException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode patched =
        patch.apply(objectMapper.convertValue(targetAccount, JsonNode.class));
    return objectMapper.treeToValue(patched, Account.class);
  }
}
