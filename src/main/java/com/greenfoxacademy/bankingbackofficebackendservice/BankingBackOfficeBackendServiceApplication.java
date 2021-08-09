package com.greenfoxacademy.bankingbackofficebackendservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class BankingBackOfficeBackendServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(BankingBackOfficeBackendServiceApplication.class, args);
  }

}
