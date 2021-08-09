package com.greenfoxacademy.bankingbackofficebackendservice.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@Table(name = "transaction")
public class Transaction extends BankApplicationProperty{

  @NotBlank
  @Column(nullable = false, length = 34)
  private String sourceAccountNumber;

  @NotBlank
  @Column(nullable = false, length = 34)
  private String targetAccountNumber;

  @NotBlank
  @Size(min = 3, max = 3)
  @Column(nullable = false)
  private String sourceCurrency;


  @Size(min = 3, max = 3)
  @NotBlank
  @Column(nullable = false)
  private String targetCurrency;

  @Column(name = "date", nullable = false, columnDefinition = "TIMESTAMP")
  private LocalDateTime date = LocalDateTime.now();

  @NotNull
  @Positive
  @Column(nullable = false)
  private Double amount;

  @NotNull
  @Column(nullable = false)
  private Double exchangeRate;
}
