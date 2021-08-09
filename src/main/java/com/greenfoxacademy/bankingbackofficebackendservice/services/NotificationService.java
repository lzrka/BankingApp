package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;

public interface NotificationService {

  void notifyClientsOnSuccess(Transaction transaction);
}
