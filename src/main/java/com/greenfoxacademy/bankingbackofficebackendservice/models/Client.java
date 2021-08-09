package com.greenfoxacademy.bankingbackofficebackendservice.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "client", uniqueConstraints = @UniqueConstraint(columnNames = "pin", name = "UniquePin"))
@SuppressWarnings("JpaDataSourceORMInspection")
public class Client extends BankApplicationProperty {

  @ApiModelProperty(notes = "clientID should be an automatically generated UUID")
  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String clientID = UUID.randomUUID().toString();

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String name;

  @ApiModelProperty(notes = "Email should be a valid email address", required = true)
  @NotBlank
  @Size(max = 255)
  @Email
  @Column(nullable = false)
  private String email;

  @ApiModelProperty(notes = "Phone numbers must begin with a country code followed by 7 digits (eg. +36307891234)", required = true)
  @NotBlank
  @Pattern(regexp = "^\\+[0-9]+$", message = "must be valid phone number (+36xxxxxxx)")
  @Column(nullable = false)
  private String phone;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String address;

  @ApiModelProperty(notes = "Birth date should be in the format 'yyyy-MM-dd' (eg. 1967-12-10)", required = true)
  @NotNull
  @Past
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  @Column(nullable = false, columnDefinition = "DATE")
  private LocalDate birthDate;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String pin;

  @JsonManagedReference
  @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
  @Column(nullable = false)
  private Set<Account> accounts;

}
