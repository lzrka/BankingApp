package com.greenfoxacademy.bankingbackofficebackendservice.models.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Data;

@Data
public class TransferDTO {

  @NotBlank
  private String targetAccountNumber;

  @NotNull
  @Positive
  private Double amount;

}
