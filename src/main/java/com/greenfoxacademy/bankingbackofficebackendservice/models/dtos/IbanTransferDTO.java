package com.greenfoxacademy.bankingbackofficebackendservice.models.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class IbanTransferDTO {

  @NotBlank
  @Pattern(regexp = "[a-zA-Z]{2}[0-9]{2}[a-zA-Z0-9]{4}[0-9]{7}([a-zA-Z0-9]?){0,16}", message = "must be valid IBAN")
  private String targetAccountIban;

  @NotNull
  @Positive
  private Double amount;

  @Size(min = 3, max = 3)
  @NotBlank
  private String currency;

}
