package com.greenfoxacademy.bankingbackofficebackendservice.aspect;

import com.greenfoxacademy.bankingbackofficebackendservice.models.Transaction;
import com.greenfoxacademy.bankingbackofficebackendservice.services.NotificationService;
import com.greenfoxacademy.bankingbackofficebackendservice.services.TransactionService;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransferServiceAspect {

  @Autowired
  private TransactionService transactionServiceImpl;

  @Autowired
  private NotificationService notificationServiceImpl;

  @AfterReturning(
      pointcut = "execution(* com.greenfoxacademy.bankingbackofficebackendservice.services.TransferService*.transferMoney*(..))",
      returning = "result")
  public void afterTransferMoneyAdvice(Transaction result) {
    transactionServiceImpl.createTransaction(result);
    notificationServiceImpl.notifyClientsOnSuccess(result);
  }
}
