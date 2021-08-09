package com.greenfoxacademy.bankingbackofficebackendservice.controllers;


import com.greenfoxacademy.bankingbackofficebackendservice.services.TransactionService;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize(value = "hasRole('ROLE_Api.USER')")
@RequestMapping("/api")
public class TransactionController {

  @Autowired
  private TransactionService transactionService;

  @ApiOperation(value = "View a list of all the Transactions from the last 90 days", notes = "Search Transactions and filtered transaction in the last 90 days")
  @GetMapping("/transactions")
  public ResponseEntity<List<Transaction>> transactions(@RequestParam(required = false) String sourceAccount, @RequestParam(required = false) String targetAccount) {
    if (sourceAccount == null && targetAccount == null) {
      return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
    } else if (sourceAccount != null && targetAccount == null) {
      return new ResponseEntity<>(transactionService.getAllTransactionsFromSourceAccount(sourceAccount), HttpStatus.OK);
   } else if (sourceAccount == null && targetAccount != null) {
      return new ResponseEntity<>(transactionService.getAllTransactionsFromTargetAccount(targetAccount), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(transactionService.getAllTransactionsFromSourceAccountAndTargetAccount(sourceAccount, targetAccount), HttpStatus.OK);
    }
  }
}
