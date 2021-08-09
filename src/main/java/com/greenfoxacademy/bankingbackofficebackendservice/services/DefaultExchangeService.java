package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.soap.MNBArfolyamServiceSoap;
import com.greenfoxacademy.bankingbackofficebackendservice.soap.MNBArfolyamServiceSoapGetCurrentExchangeRatesStringFaultFaultMessage;
import com.greenfoxacademy.bankingbackofficebackendservice.soap.MNBArfolyamServiceSoapImpl;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class DefaultExchangeService implements ExchangeService {

  private final MNBArfolyamServiceSoapImpl impl = new MNBArfolyamServiceSoapImpl();
  private final MNBArfolyamServiceSoap exchangeRateService =
      impl.getCustomBindingMNBArfolyamServiceSoap();

  @Override
  public Double getCurrentHufToCurrencyRate(String toCurrency) {
    Double exchangeRate = null;
    if (toCurrency.equals("HUF")) {
      return 1.0;
    }
    try {
      exchangeRate =
          parseExchangeRatesResponse(exchangeRateService.getCurrentExchangeRates(), toCurrency);
    } catch (MNBArfolyamServiceSoapGetCurrentExchangeRatesStringFaultFaultMessage mnbArfolyamServiceSoapGetCurrentExchangeRatesStringFaultFaultMessage) {
      mnbArfolyamServiceSoapGetCurrentExchangeRatesStringFaultFaultMessage.printStackTrace();
    }
    return exchangeRate;
  }

  @Override
  public Double fromCurrencyToCurrencyConversion(Double amount, String fromCurrency,
                                                 String toCurrency) {
    return amount * getCurrentCurrencyToCurrencyRate(fromCurrency, toCurrency);
  }

  @Override
  public Double getCurrentCurrencyToCurrencyRate(String fromCurrency, String toCurrency) {
    Double rateA = getCurrentHufToCurrencyRate(fromCurrency);
    Double rateB = getCurrentHufToCurrencyRate(toCurrency);
    return rateA / rateB;
  }

  private Double parseExchangeRatesResponse(String response, String currency) {
    Double parsedRate = null;
    String pattern = '"' + currency + '"';

    String foundRate = Stream.of(response.split("</Rate>"))
        .filter(e -> e.contains(pattern))
        .findAny()
        .orElseThrow(() -> new IllegalArgumentException("Not a valid currency name"));

    if (foundRate != null) {
      String[] split = foundRate.split(pattern + ">");
      String exchangeRate = split[1].replace(',', '.');
      parsedRate = Double.parseDouble(exchangeRate);
    }
    return parsedRate;
  }
}
