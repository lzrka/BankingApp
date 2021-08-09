package com.greenfoxacademy.bankingbackofficebackendservice.models;

import io.swagger.annotations.ApiModelProperty;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@MappedSuperclass
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BankApplicationProperty {

  @ApiModelProperty(value = "primary key for entity", dataType = "Integer", notes = "should be automatically generated")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;


}

