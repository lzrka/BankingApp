package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;

public interface TransferService {

  Transaction transferMoneyWithinTheBank(int clientId, String targetAccountNumber,
                                         Double valueInTargetCurrency);

  Transaction transferMoneyToAnotherBank(int clientId, String targetAccountIBAN,
                                  Double valueInTargetCurrency, String targetCurrency);
}
