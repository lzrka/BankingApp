package com.greenfoxacademy.bankingbackofficebackendservice.services;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.BranchRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BranchService {

  @Autowired
  private BranchRepository branchRepository;

  @Transactional
  public Branch createBranch(Branch branch) {
    Objects.requireNonNull(branch);
    Integer branchId = branch.getId();
    if (branchId != null && existsById(branchId)) {
      throw new ResourceAlreadyExistsException("Branch with id: " + branchId + " already exists");
    } else {
      return branchRepository.save(branch);
    }
  }

  @Transactional
  public Branch updateBranch(Branch branch) {
    Objects.requireNonNull(branch);
    Integer branchId = branch.getId();
    if (!existsById(branchId)) {
      throw new ResourceNotFoundException("Branch with id: " + branchId + " doesn't exists");
    }
    return branchRepository.save(branch);
  }

  public List<Branch> getAllBranches() {
    return branchRepository.findAll();
  }

  public Branch getBranchById(Integer branchId) {
    Objects.requireNonNull(branchId);
    return branchRepository.findById(branchId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Branch with id: " + branchId + " doesn't exists"));
  }

  @Transactional
  public void removeBranch(Integer branchId) {
    Objects.requireNonNull(branchId);
    if (!existsById(branchId)) {
      throw new ResourceNotFoundException("Branch with id: " + branchId + " doesn't exists");
    }
    branchRepository.deleteById(branchId);
  }

  private boolean existsById(Integer id) {
    return branchRepository.existsById(id);
  }
}
