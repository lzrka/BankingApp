package com.greenfoxacademy.bankingbackofficebackendservice.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Table(name = "employee")
public class Employee extends BankApplicationProperty {

  @ApiModelProperty(notes="Name can't be empty")
  @NotBlank
  @Column(nullable = false)
  private String name;

  @ApiModelProperty(notes="Branch can't be null")
  @JsonIgnoreProperties({"employees"})
  @NotNull
  @ManyToOne
  @JoinColumn(name = "branch_id", nullable = false, foreignKey = @ForeignKey(name = "FK_EMPLOYEE_BRANCH"))
  private Branch branch;
}
