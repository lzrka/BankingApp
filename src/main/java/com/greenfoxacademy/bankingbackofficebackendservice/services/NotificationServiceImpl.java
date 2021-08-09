package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Client;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.AccountRepository;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Autowired
  private JavaMailSender mailSender;

  @Autowired
  private AccountRepository accountRepository;

  public void notifyClientsOnSuccess(Transaction transaction) {
    Client sourceClient =
        getAccountByAccountNumber(transaction.getSourceAccountNumber()).getClient();
    Client targetClient =
        getAccountByAccountNumber(transaction.getTargetAccountNumber()).getClient();

    sendMail(sourceClient.getName(), sourceClient.getEmail(), transaction);
    sendMail(targetClient.getName(), targetClient.getEmail(), transaction);
  }

  private void sendMail(String clientName, String address, Transaction tx) {
    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("bankingofficeapp@yahoo.com");
    message.setTo(address);
    message.setSubject("Successful transaction notification");
    message.setText("Dear " + clientName + ",\n"
        + "There was a successful transaction in our system at " + tx.getDate().format(formatter) +
        ".\n\n"
        + "Source Account: " + tx.getSourceAccountNumber() + "\n"
        + "Target Account: " + tx.getTargetAccountNumber() + "\n"
        + "Amount: " + tx.getAmount() + "\n"
        + "Source currency: " + tx.getSourceCurrency() + "\n"
        + "Target currency: " + tx.getTargetCurrency() + "\n"
        + "Exchange rate: " + tx.getExchangeRate() + "\n"
    );
    mailSender.send(message);
  }

  private Account getAccountByAccountNumber(String accountNumber) {
    return accountRepository.findByAccountNumber(accountNumber)
        .orElseThrow(
            () -> new ResourceNotFoundException(
                "Account with accountNumber: " + accountNumber + " doesn't exist"));
  }

}
