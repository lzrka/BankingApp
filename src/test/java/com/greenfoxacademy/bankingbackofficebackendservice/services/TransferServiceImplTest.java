package com.greenfoxacademy.bankingbackofficebackendservice.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class TransferServiceImplTest {
  
  @Autowired
  private TransferService transferServiceImpl;
  
  @MockBean
  private AccountService accountServiceImpl;
  
  @MockBean
  private ExchangeService defaultExchangeService;
  
  @Test
  void transferMoneyWithinTheBank() {
    Client client1 = Client.builder().id(0).build();
    Client client2 = Client.builder().id(1).build();
    int clientId = 5;
    String targetAccountNumber = "12345678-12345678";
    Double amount = 2.0;
    Double convertedAmount = 600.0;
    Account sender = Account.builder()
                              .id(5)
                              .accountNumber("112233445566778899112233")
                              .client(client1)
                              .currency(Currency.getInstance("HUF"))
                              .value(1000L)
                              .build();
    
    Account receiver = Account.builder()
                              .id(2)
                              .accountNumber("112233445566778811223344")
                              .client(client2)
                              .currency(Currency.getInstance("USD"))
                              .value(2000L)
                              .build();
    
    when(accountServiceImpl.getAccountById(clientId)).thenReturn(sender);
    when(accountServiceImpl.getAccountByAccountNumber(targetAccountNumber)).thenReturn(receiver);
    when(defaultExchangeService
        .fromCurrencyToCurrencyConversion(amount, sender.getCurrency().toString(), receiver.getCurrency().toString())).thenReturn(convertedAmount);
    
    transferServiceImpl.transferMoneyWithinTheBank(clientId, targetAccountNumber, amount);
  
    assertEquals(400, sender.getValue());
    assertEquals(2002, receiver.getValue());
  }
  
  @Test
  void transferMoneyToAnotherBank() {
    Client client1 = Client.builder().id(0).build();
    int clientId = 5;
    String targetAccountIBAN = "HU12345678910111213456524";
    Double amount = 2.0;
    Double convertedAmount = 600.0;
    String targetCurrency = "USD";
    Account sender = Account.builder()
                            .id(5)
                            .accountNumber("112233445566778899112233")
                            .client(client1)
                            .currency(Currency.getInstance("HUF"))
                            .value(1000L)
                            .build();
  
    when(accountServiceImpl.getAccountById(clientId)).thenReturn(sender);
    when(defaultExchangeService
        .fromCurrencyToCurrencyConversion(amount, sender.getCurrency().toString(), targetCurrency)).thenReturn(convertedAmount);
    
    transferServiceImpl
        .transferMoneyToAnotherBank(clientId,targetAccountIBAN, amount, targetCurrency);
    
    assertEquals(400, sender.getValue());
  }
}
