package com.greenfoxacademy.bankingbackofficebackendservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.TransactionRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class TransactionServiceTest {

  @Autowired
  private TransactionService transactionService;

  @MockBean
  private TransactionRepository transactionRepository;

  @Test
  public void getAllTransactionsTestSuccess() {
    List<Transaction> transactions = new ArrayList<>();

    Transaction transaction1 = Transaction.builder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(LocalDateTime.of(
            LocalDate.of(2020, 11, 2),
            LocalTime.of(10, 29, 2, 652)))
        .amount(200.0)
        .exchangeRate(300.0)
        .build();
    Transaction transaction2 = Transaction.builder()
        .id(2)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("HUF")
        .targetCurrency("EUR")
        .date(LocalDateTime.of(
            LocalDate.of(2020, 11, 3),
            LocalTime.of(10, 30, 2, 652)))
        .amount(51000.0)
        .exchangeRate(0.008)
        .build();

    transactions.add(transaction1);
    transactions.add(transaction2);

    doReturn(transactions).when(transactionRepository).findAllByDateAfter(Mockito.any(LocalDateTime.class));

    List<Transaction> expectedList = transactionService.getAllTransactions();

    assertEquals(2, expectedList.size());
    assertEquals(expectedList, transactions);
    verify(transactionRepository, times(1)).findAllByDateAfter(Mockito.any(LocalDateTime.class));
  }

  @Test
  public void createTransactionTestSuccess() {
    Transaction transaction1 = Transaction.builder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(LocalDateTime.of(
            LocalDate.of(2020, 11, 2),
            LocalTime.of(10, 29, 2, 652)))
        .amount(200.0)
        .exchangeRate(300.0)
        .build();

    when(transactionRepository.save(transaction1)).thenReturn(transaction1);

    Transaction transaction = transactionService.createTransaction(transaction1);

    assertEquals(1, transaction.getId());
    assertEquals("112233445566778899112233", transaction.getSourceAccountNumber());
    assertEquals("112233445566778811223344", transaction.getTargetAccountNumber());
    assertEquals("USD", transaction.getSourceCurrency());
    assertEquals("HUF", transaction.getTargetCurrency());
    assertEquals(LocalDateTime.of(
        LocalDate.of(2020, 11, 2),
        LocalTime.of(10, 29, 2, 652)), transaction.getDate());
    assertEquals(200.0, transaction.getAmount());
    assertEquals(300.0, transaction.getExchangeRate());
  }

  @Test
  public void createTransactionTestAlreadyExists() {
    Transaction transaction1 = Transaction.builder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(LocalDateTime.of(
            LocalDate.of(2020, 11, 2),
            LocalTime.of(10, 29, 2, 652)))
        .amount(200.0)
        .exchangeRate(300.0)
        .build();

    when(transactionRepository.existsById(transaction1.getId())).thenReturn(true);

    Exception exception = assertThrows(ResourceAlreadyExistsException.class,
        () -> transactionService.createTransaction(transaction1));
    assertEquals("Transaction with id: " + transaction1.getId() + " already exists",
        exception.getMessage());
  }

  @Test
  public void getAllTransactionsFromSourceAccountTest() {
    List<Transaction> transactions = new ArrayList<>();

    Transaction transaction1 = Transaction.builder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(LocalDateTime.of(
            LocalDate.of(2020, 11, 2),
            LocalTime.of(10, 29, 2, 652)))
        .amount(200.0)
        .exchangeRate(300.0)
        .build();

    transactions.add(transaction1);

    doReturn(transactions).when(transactionRepository)
        .findTransactionBySourceAccountNumberAndDateAfter(Mockito.eq("112233445566778899112233"), Mockito.any(LocalDateTime.class));


    List<Transaction> expectedList =
        transactionService.getAllTransactionsFromSourceAccount("112233445566778899112233");

    assertEquals(1, expectedList.size());
    assertEquals(expectedList, transactions);
    verify(transactionRepository, times(1))
        .findTransactionBySourceAccountNumberAndDateAfter(Mockito.eq("112233445566778899112233"), Mockito.any(LocalDateTime.class));
  }

  @Test
  public void getAllTransactionsFromTargetAccountTest() {
    List<Transaction> transactions = new ArrayList<>();

    Transaction transaction1 = Transaction.builder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(LocalDateTime.of(
            LocalDate.of(2020, 11, 2),
            LocalTime.of(10, 29, 2, 652)))
        .amount(200.0)
        .exchangeRate(300.0)
        .build();

    transactions.add(transaction1);

    when(transactionRepository.findTransactionByTargetAccountNumberAndDateAfter(
        Mockito.eq("112233445566778811223344"), Mockito.any(LocalDateTime.class)))
        .thenReturn(transactions);

    List<Transaction> expectedList = transactionService
        .getAllTransactionsFromTargetAccount("112233445566778811223344");

    assertEquals(1, expectedList.size());
    assertEquals(expectedList, transactions);
    verify(transactionRepository, times(1))
        .findTransactionByTargetAccountNumberAndDateAfter(Mockito.eq("112233445566778811223344"), Mockito.any(LocalDateTime.class));
  }

  @Test
  public void getAllTransactionsFromSourceAccountAndTargetAccountTest() {
    List<Transaction> transactions = new ArrayList<>();

    Transaction transaction1 = Transaction.builder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(LocalDateTime.of(
            LocalDate.of(2020, 11, 2),
            LocalTime.of(10, 29, 2, 652)))
        .amount(200.0)
        .exchangeRate(300.0)
        .build();

    transactions.add(transaction1);

    when(transactionRepository.findTransactionBySourceAccountNumberAndTargetAccountNumberAndDateAfter(
        Mockito.eq("112233445566778899112233"),
        Mockito.eq("112233445566778811223344"),
        Mockito.any(LocalDateTime.class)))
        .thenReturn(transactions);

    List<Transaction> expectedList = transactionService
        .getAllTransactionsFromSourceAccountAndTargetAccount(
            "112233445566778899112233", "112233445566778811223344");

    assertEquals(1, expectedList.size());
    assertEquals(expectedList, transactions);
    verify(transactionRepository, times(1))
        .findTransactionBySourceAccountNumberAndTargetAccountNumberAndDateAfter(
            Mockito.eq("112233445566778899112233"),
                Mockito.eq("112233445566778811223344"),
            Mockito.any(LocalDateTime.class));;
  }
}
