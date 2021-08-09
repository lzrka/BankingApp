package com.greenfoxacademy.bankingbackofficebackendservice.services;

public interface ExchangeService {

  Double getCurrentHufToCurrencyRate(String toCurrency);

  Double fromCurrencyToCurrencyConversion(Double amount, String fromCurrency,
                                          String toCurrency);

  Double getCurrentCurrencyToCurrencyRate(String fromCurrency, String toCurrency);
}
