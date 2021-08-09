package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.BadResourceException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Account;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransferServiceImpl implements TransferService {

  @Autowired
  private AccountService accountServiceImpl;

  @Autowired
  private ExchangeService defaultExchangeService;

  @Override
  @Transactional
  public Transaction transferMoneyWithinTheBank(int clientId, String targetAccountNumber,
                                                Double amount) {
    Account sender = accountServiceImpl.getAccountById(clientId);
    if (sender.getAccountNumber().equals(targetAccountNumber)) {
      throw new BadResourceException("Source account matches the target account");
    }
    Account receiver =
        accountServiceImpl.getAccountByAccountNumber(targetAccountNumber);

    Long senderValue = sender.getValue();
    Long receiverValue = receiver.getValue();
    Double exchangeRate = defaultExchangeService
        .getCurrentCurrencyToCurrencyRate(receiver.getCurrency().toString(),
            sender.getCurrency().toString());
    Double convertedAmount = amount * exchangeRate;

    if (convertedAmount > senderValue) {
      throw new BadResourceException(
          "You don't have enough money on your balance to complete this transaction");
    }

    sender.setValue((long) (senderValue - convertedAmount));
    accountServiceImpl.updateAccount(sender);
    receiver.setValue((long) (receiverValue + amount));
    accountServiceImpl.updateAccount(receiver);

    return buildTransaction(sender, receiver, amount, exchangeRate);
  }

  @Override
  @Transactional
  public Transaction transferMoneyToAnotherBank(int clientId, String targetAccountIBAN,
                                                Double amount, String targetCurrency) {
    Account sender = accountServiceImpl.getAccountById(clientId);
    Long senderValue = sender.getValue();
    if (amount > senderValue) {
      throw new BadResourceException(
          "You don't have enough money on your balance to complete this transaction");
    }

    Double exchangeRate = defaultExchangeService
        .getCurrentCurrencyToCurrencyRate(sender.getCurrency().toString(), targetCurrency);
    Double convertedAmount = amount * exchangeRate;

    sender.setValue((long) (senderValue - convertedAmount));
    accountServiceImpl.updateAccount(sender);

    System.out.println("============= SUCCESSFUL TRANSACTION ============= ");
    System.out.println("FROM ACCOUNT: " + sender.getAccountNumber());
    System.out.println("TO ACCOUNT: " + targetAccountIBAN);
    System.out.println("AMOUNT: " + convertedAmount + " " + targetCurrency);

    return buildTransaction(sender, targetAccountIBAN, targetCurrency, amount, exchangeRate);
  }

  private Transaction buildTransaction(Account source, Account target, Double amount,
                                       Double exchangeRate) {
    return new Transaction().toBuilder()
        .sourceAccountNumber(source.getAccountNumber())
        .sourceCurrency(source.getCurrency().toString())
        .targetAccountNumber(target.getAccountNumber())
        .targetCurrency(target.getCurrency().toString())
        .amount(amount)
        .exchangeRate(exchangeRate)
        .build();
  }

  private Transaction buildTransaction(Account source, String targetIBAN, String targetCurrency,
                                       Double amount,
                                       Double exchangeRate) {
    return new Transaction().toBuilder()
        .sourceAccountNumber(source.getAccountNumber())
        .sourceCurrency(source.getCurrency().toString())
        .targetAccountNumber(targetIBAN)
        .targetCurrency(targetCurrency)
        .amount(amount)
        .exchangeRate(exchangeRate)
        .build();
  }

}
