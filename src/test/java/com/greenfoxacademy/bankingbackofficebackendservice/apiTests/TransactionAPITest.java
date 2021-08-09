package com.greenfoxacademy.bankingbackofficebackendservice.apiTests;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import com.greenfoxacademy.bankingbackofficebackendservice.services.TransactionService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.Email;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionAPITest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TransactionService transactionService;

  @Test
  @DisplayName("Test GET '/api/transactions' 200 OK")
  public void getsAllTransactions() throws Exception {
    LocalDateTime txDate = LocalDateTime.now().minusDays(10);
    Transaction txA = new Transaction().toBuilder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(txDate)
        .amount(200.0)
        .exchangeRate(0.008)
        .build();

    Transaction txB = new Transaction().toBuilder()
        .id(2)
        .sourceAccountNumber("112233445566778811223344")
        .targetAccountNumber("112233445566778899112233")
        .sourceCurrency("HUF")
        .targetCurrency("USD")
        .date(txDate)
        .amount(100.0)
        .exchangeRate(0.008)
        .build();

    Mockito.when(transactionService.getAllTransactions()).thenReturn(Lists.newArrayList(txA, txB));

    mockMvc.perform(get("/api/transactions"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].sourceAccountNumber", is("112233445566778899112233")))
        .andExpect(jsonPath("$[0].targetAccountNumber", is("112233445566778811223344")))
        .andExpect(jsonPath("$[0].sourceCurrency", is("USD")))
        .andExpect(jsonPath("$[0].targetCurrency", is("HUF")))
        .andExpect(jsonPath("$[0].date", is(txDate.toString())))
        .andExpect(jsonPath("$[0].amount", is(200.0)))
        .andExpect(jsonPath("$[0].exchangeRate", is(0.008)))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].sourceAccountNumber", is("112233445566778811223344")))
        .andExpect(jsonPath("$[1].targetAccountNumber", is("112233445566778899112233")))
        .andExpect(jsonPath("$[1].sourceCurrency", is("HUF")))
        .andExpect(jsonPath("$[1].targetCurrency", is("USD")))
        .andExpect(jsonPath("$[1].date", is(txDate.toString())))
        .andExpect(jsonPath("$[1].amount", is(100.0)))
        .andExpect(jsonPath("$[1].exchangeRate", is(0.008)));

    verify(transactionService, times(1)).getAllTransactions();
    verifyNoMoreInteractions(transactionService);

  }

  @Test
  @DisplayName("Test GET '/api/transactions' 200 OK when database is empty")
  public void getsAllTransactionsWhenDbIsEmpty() throws Exception {
    ArrayList<Transaction> emptyList = new ArrayList<>();

    Mockito.when(transactionService.getAllTransactions()).thenReturn(emptyList);

    mockMvc.perform(get("/api/transactions"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(0)));

    verify(transactionService, times(1)).getAllTransactions();
    verifyNoMoreInteractions(transactionService);
  }

  @Test
  @DisplayName("Test GET '/api/transactions?sourceAccount={sourceAccountNumber}' 200 OK")
  public void getsAllTransactionsWithSourceAccountQueryParam() throws Exception {
    LocalDateTime txDate = LocalDateTime.now().minusDays(10);
    Transaction txA = new Transaction().toBuilder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(txDate)
        .amount(200.0)
        .exchangeRate(0.008)
        .build();

    Mockito.when(transactionService.getAllTransactionsFromSourceAccount(txA.getSourceAccountNumber())).thenReturn(Lists.newArrayList(txA));

    mockMvc.perform(get("/api/transactions?sourceAccount={sourceAccountNumber}", txA.getSourceAccountNumber()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].sourceAccountNumber", is("112233445566778899112233")))
        .andExpect(jsonPath("$[0].targetAccountNumber", is("112233445566778811223344")))
        .andExpect(jsonPath("$[0].sourceCurrency", is("USD")))
        .andExpect(jsonPath("$[0].targetCurrency", is("HUF")))
        .andExpect(jsonPath("$[0].date", is(txDate.toString())))
        .andExpect(jsonPath("$[0].amount", is(200.0)))
        .andExpect(jsonPath("$[0].exchangeRate", is(0.008)));

    verify(transactionService, times(1)).getAllTransactionsFromSourceAccount(txA.getSourceAccountNumber());
    verifyNoMoreInteractions(transactionService);
  }

  @Test
  @DisplayName("Test GET '/api/transactions?targetAccount={targetAccountNumber}' 200 OK")
  public void getsAllTransactionsWithTargetAccountQueryParam() throws Exception {
    LocalDateTime txDate = LocalDateTime.now().minusDays(10);
    Transaction txA = new Transaction().toBuilder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(txDate)
        .amount(200.0)
        .exchangeRate(0.008)
        .build();

    Mockito.when(transactionService.getAllTransactionsFromTargetAccount(txA.getTargetAccountNumber())).thenReturn(Lists.newArrayList(txA));

    mockMvc.perform(get("/api/transactions?targetAccount={targetAccountNumber}", txA.getTargetAccountNumber()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].sourceAccountNumber", is("112233445566778899112233")))
        .andExpect(jsonPath("$[0].targetAccountNumber", is("112233445566778811223344")))
        .andExpect(jsonPath("$[0].sourceCurrency", is("USD")))
        .andExpect(jsonPath("$[0].targetCurrency", is("HUF")))
        .andExpect(jsonPath("$[0].date", is(txDate.toString())))
        .andExpect(jsonPath("$[0].amount", is(200.0)))
        .andExpect(jsonPath("$[0].exchangeRate", is(0.008)));

    verify(transactionService, times(1)).getAllTransactionsFromTargetAccount(txA.getTargetAccountNumber());
    verifyNoMoreInteractions(transactionService);
  }

  @Test
  @DisplayName("Test GET '/api/transactions?sourceAccount={sourceAccountNumber}&targetAccount={targetAccountNumber}' 200 OK")
  public void getsAllTransactionsWithTargetAndSourceAccountQueryParams() throws Exception {
    LocalDateTime txDate = LocalDateTime.now().minusDays(10);
    Transaction txA = new Transaction().toBuilder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778811223344")
        .sourceCurrency("USD")
        .targetCurrency("HUF")
        .date(txDate)
        .amount(200.0)
        .exchangeRate(0.008)
        .build();

    Mockito.when(transactionService.getAllTransactionsFromSourceAccountAndTargetAccount(txA.getSourceAccountNumber(),
        txA.getTargetAccountNumber())).thenReturn(Lists.newArrayList(txA));

    mockMvc.perform(get("/api/transactions?sourceAccount={sourceAccountNumber}&targetAccount={targetAccountNumber}", txA.getSourceAccountNumber(),txA.getTargetAccountNumber()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].sourceAccountNumber", is("112233445566778899112233")))
        .andExpect(jsonPath("$[0].targetAccountNumber", is("112233445566778811223344")))
        .andExpect(jsonPath("$[0].sourceCurrency", is("USD")))
        .andExpect(jsonPath("$[0].targetCurrency", is("HUF")))
        .andExpect(jsonPath("$[0].date", is(txDate.toString())))
        .andExpect(jsonPath("$[0].amount", is(200.0)))
        .andExpect(jsonPath("$[0].exchangeRate", is(0.008)));

    verify(transactionService, times(1)).getAllTransactionsFromSourceAccountAndTargetAccount(txA.getSourceAccountNumber(),
        txA.getTargetAccountNumber());
    verifyNoMoreInteractions(transactionService);
  }

}
