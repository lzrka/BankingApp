package com.greenfoxacademy.bankingbackofficebackendservice.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Table(name = "branch", uniqueConstraints = {
    @UniqueConstraint(name = "UniqueCityAndAddress", columnNames = {"city", "address"})})
@SuppressWarnings("JpaDataSourceORMInspection")
public class Branch extends BankApplicationProperty {

  @NotBlank
  @Size(max = 50)
  @Column(nullable = false)
  private String zip;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String city;

  @NotBlank
  @Size(max = 255)
  @Column(nullable = false)
  private String address;

  @JsonIgnoreProperties({"branch"})
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "branch")
  private Set<Employee> employees;

}
