package com.greenfoxacademy.bankingbackofficebackendservice.services;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.AccountRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Currency;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@SpringBootTest
class NotificationServiceTest {

  @Autowired
  private NotificationService notificationService;

  @MockBean
  private AccountRepository mockAccountRepository;

  @MockBean
  private JavaMailSender mockMailSender;

  @Test
  @DisplayName("Test sending email notifications after a successful transaction")
  public void testSendingMailToSourceSuccess() {
    Transaction tx = new Transaction().toBuilder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778855443322")
        .sourceCurrency("USD")
        .targetCurrency("EUR")
        .date(LocalDateTime.parse("2021-07-21T20:49:28.651"))
        .amount(20.0)
        .exchangeRate(1.1780907155722942)
        .build();

    Client clientA = Client.builder().id(22).clientID("559765KA").name("Jóska Darab")
        .email("joska.darab@gmail.com")
        .phone("+36301254697").address("Nefelejts Utca 10.").birthDate(
            LocalDate.parse("1990-05-10")).pin("1542").build();

    Client clientB = Client.builder().id(20).clientID("731245MN").name("Béla Kosárfonó")
        .email("bela.kosarfono@citromail.hu")
        .phone("+36704659878").address("Kung-fu Panda Utca 54.").birthDate(
            LocalDate.parse("1975-03-20")).pin("4697").build();

    Account sourceAccount = Account.builder().id(1).accountNumber("112233445566778899112233")
        .currency(Currency.getInstance("USD")).value(5000L).client(clientA).build();

    Account targetAccount = Account.builder().id(2).accountNumber("112233445566778855443322")
        .currency(Currency.getInstance("EUR")).value(4000L).client(clientB).build();

    when(mockAccountRepository.findByAccountNumber(tx.getSourceAccountNumber())).thenReturn(
        java.util.Optional.ofNullable(sourceAccount));
    when(mockAccountRepository.findByAccountNumber(tx.getTargetAccountNumber())).thenReturn(
        java.util.Optional.ofNullable(targetAccount));

    notificationService.notifyClientsOnSuccess(tx);

    verify(mockMailSender, times(2)).send(any(SimpleMailMessage.class));
    verifyNoMoreInteractions(mockMailSender);
  }

  @Test
  @DisplayName("Test sending the correct email messages after a successful transaction")
  public void testSendingCorrectEmailMessage() {
    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    Transaction tx = new Transaction().toBuilder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778855443322")
        .sourceCurrency("USD")
        .targetCurrency("EUR")
        .date(LocalDateTime.parse("2021-07-21T20:49:28.651"))
        .amount(20.0)
        .exchangeRate(1.1780907155722942)
        .build();

    Client clientA = Client.builder().id(22).clientID("559765KA").name("Jóska Darab")
        .email("joska.darab@gmail.com")
        .phone("+36301254697").address("Nefelejts Utca 10.").birthDate(
            LocalDate.parse("1990-05-10")).pin("1542").build();

    Client clientB = Client.builder().id(20).clientID("731245MN").name("Béla Kosárfonó")
        .email("bela.kosarfono@citromail.hu")
        .phone("+36704659878").address("Kung-fu Panda Utca 54.").birthDate(
            LocalDate.parse("1975-03-20")).pin("4697").build();

    Account sourceAccount = Account.builder().id(1).accountNumber("112233445566778899112233")
        .currency(Currency.getInstance("USD")).value(5000L).client(clientA).build();

    Account targetAccount = Account.builder().id(2).accountNumber("112233445566778855443322")
        .currency(Currency.getInstance("EUR")).value(4000L).client(clientB).build();

    ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor =
        ArgumentCaptor.forClass(SimpleMailMessage.class);

    when(mockAccountRepository.findByAccountNumber(tx.getSourceAccountNumber())).thenReturn(
        java.util.Optional.ofNullable(sourceAccount));
    when(mockAccountRepository.findByAccountNumber(tx.getTargetAccountNumber())).thenReturn(
        java.util.Optional.ofNullable(targetAccount));

    notificationService.notifyClientsOnSuccess(tx);

    verify(mockMailSender, times(2)).send(mailMessageArgumentCaptor.capture());

    SimpleMailMessage mailMessageA = mailMessageArgumentCaptor.getAllValues().get(0);
    SimpleMailMessage mailMessageB = mailMessageArgumentCaptor.getAllValues().get(1);

    Assertions.assertEquals("bankingofficeapp@yahoo.com", mailMessageA.getFrom());
    Assertions.assertNotNull(mailMessageA.getTo());
    Assertions.assertEquals(1, mailMessageA.getTo().length);
    Assertions.assertEquals("Successful transaction notification", mailMessageA.getSubject());
    Assertions.assertEquals("Dear " + clientA.getName() + ",\n"
        + "There was a successful transaction in our system at " + tx.getDate().format(formatter) +
        ".\n\n"
        + "Source Account: " + tx.getSourceAccountNumber() + "\n"
        + "Target Account: " + tx.getTargetAccountNumber() + "\n"
        + "Amount: " + tx.getAmount() + "\n"
        + "Source currency: " + tx.getSourceCurrency() + "\n"
        + "Target currency: " + tx.getTargetCurrency() + "\n"
        + "Exchange rate: " + tx.getExchangeRate() + "\n", mailMessageA.getText());

    Assertions.assertEquals("bankingofficeapp@yahoo.com", mailMessageB.getFrom());
    Assertions.assertNotNull(mailMessageB.getTo());
    Assertions.assertEquals(1, mailMessageB.getTo().length);
    Assertions.assertEquals("Successful transaction notification", mailMessageB.getSubject());
    Assertions.assertEquals("Dear " + clientB.getName() + ",\n"
        + "There was a successful transaction in our system at " + tx.getDate().format(formatter) +
        ".\n\n"
        + "Source Account: " + tx.getSourceAccountNumber() + "\n"
        + "Target Account: " + tx.getTargetAccountNumber() + "\n"
        + "Amount: " + tx.getAmount() + "\n"
        + "Source currency: " + tx.getSourceCurrency() + "\n"
        + "Target currency: " + tx.getTargetCurrency() + "\n"
        + "Exchange rate: " + tx.getExchangeRate() + "\n", mailMessageB.getText());
  }

  @Test
  @DisplayName("Test sending email notification when account number doesn't exist")
  public void testSendingMailToSourceThrowsNullPointerException() {
    Transaction tx = new Transaction().toBuilder()
        .id(1)
        .sourceAccountNumber("112233445566778899112233")
        .targetAccountNumber("112233445566778855443322")
        .sourceCurrency("USD")
        .targetCurrency("EUR")
        .date(LocalDateTime.parse("2021-07-21T20:49:28.651"))
        .amount(20.0)
        .exchangeRate(1.1780907155722942)
        .build();

    when(mockAccountRepository.findByAccountNumber(tx.getSourceAccountNumber()))
        .thenThrow(new ResourceNotFoundException(
            "Account with accountNumber: " + tx.getSourceAccountNumber() + " doesn't exist"));

    Assertions
        .assertThrows(ResourceNotFoundException.class,
            () -> notificationService.notifyClientsOnSuccess(tx));

    verify(mockMailSender, times(0)).send(any(SimpleMailMessage.class));
    verifyNoMoreInteractions(mockMailSender);
  }

}
