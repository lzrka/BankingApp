package com.greenfoxacademy.bankingbackofficebackendservice.services;

import static org.mockito.Mockito.*;

import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceAlreadyExistsException;
import com.greenfoxacademy.bankingbackofficebackendservice.exceptions.ResourceNotFoundException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import com.greenfoxacademy.bankingbackofficebackendservice.repositories.BranchRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class BranchServiceTest {

  @MockBean
  BranchRepository mockRepository;

  @Autowired
  BranchService branchService;

  @Test
  @DisplayName("Test getting all the branches WHEN there are branches in the DB")
  void testGetAllBranchesSuccess() {
    Branch branchA =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    Branch branchB =
        Branch.builder().id(2).address("Address 2.").city("City 2.").zip("2234").build();
    Branch branchC =
        Branch.builder().id(3).address("Address 3.").city("City 3.").zip("3234").build();
    Branch branchD =
        Branch.builder().id(4).address("Address 4.").city("City 4.").zip("4234").build();
    Branch branchE =
        Branch.builder().id(5).address("Address 5.").city("City 5.").zip("5234").build();

    doReturn(Arrays.asList(branchA, branchB, branchC, branchD, branchE)).when(mockRepository)
        .findAll();

    List<Branch> branches = branchService.getAllBranches();

    Assertions.assertNotNull(branches);
    Assertions.assertEquals(5, branches.size());
  }

  @Test
  @DisplayName("Test getting all the branches WHEN there are no branches in the DB")
  void testGetAllBranchesReturnsEmptyList() {
    List<Branch> noBranches = new ArrayList<>();
    doReturn(noBranches).when(mockRepository).findAll();

    List<Branch> branches = branchService.getAllBranches();
    
    Assertions.assertEquals(branches, noBranches);
    Assertions.assertEquals(0, branches.size());
  }

  @Test
  @DisplayName("Test getting correct branch with Id WHEN branch exists")
  void testGetBranchByIdReturnsCorrectBranch() {
    Branch branchA =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    doReturn(Optional.of(branchA)).when(mockRepository).findById(branchA.getId());

    Branch branch = branchService.getBranchById(1);

    Assertions.assertEquals(branch, branchA);
  }

  @Test
  @DisplayName("Test getting branch with Id WHEN branch doesn't exists")
  void testGetBranchByIdThrowsException() {
    doThrow(new ResourceNotFoundException("Branch with id: 1 doesn't exists")).when(mockRepository)
        .findById(1);

    Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      branchService.getBranchById(1);
    });

    String expectedMessage = "Branch with id: 1 doesn't exists";
    String actualMessage = exception.getMessage();

    Assertions.assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  @DisplayName("Test creating new branch successfully")
  void testCreateBranchCreatesNewBranch() {
    Branch branchA =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    doReturn(false).when(mockRepository).existsById(branchA.getId());
    doReturn(branchA).when(mockRepository).save(branchA);

    Branch actualBranch = branchService.createBranch(branchA);

    Assertions.assertEquals(branchA, actualBranch);
  }

  @Test
  @DisplayName("Test creating new branch WHEN branch already exists")
  void testCreateBranchThrowsResourceAlreadyExistsException() {
    Branch branchA =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    doThrow(new ResourceAlreadyExistsException(
        "Branch with id: " + branchA.getId() + " already exists")).when(mockRepository).existsById(
        branchA.getId());

    Exception exception = Assertions.assertThrows(ResourceAlreadyExistsException.class, () -> {
      branchService.createBranch(branchA);
    });

    String expectedMessage = "Branch with id: 1 already exists";
    String actualMessage = exception.getMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  @DisplayName("Test updating a branch successfully")
  void testUpdateBranchUpdatesBranchWithExistingId() {
    Branch branchA =
        Branch.builder().id(4).address("Address 4.").city("City 5.").zip("1234656").build();
    doReturn(true).when(mockRepository).existsById(branchA.getId());
    doReturn(branchA).when(mockRepository).save(branchA);

    Branch updatedBranch = branchService.updateBranch(branchA);

    Assertions.assertEquals(branchA, updatedBranch);
  }

  @Test
  @DisplayName("Test updating branch WHEN branch doesn't exists")
  void testUpdateBranchThrowsResourceNotFoundException() {
    Branch branchA =
        Branch.builder().id(1).address("Address 1.").city("City 1.").zip("1234").build();
    doThrow(new ResourceNotFoundException(
        "Branch with id: " + branchA.getId() + " doesn't exists")).when(mockRepository).existsById(
        branchA.getId());

    Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      branchService.updateBranch(branchA);
    });

    String expectedMessage = "Branch with id: 1 doesn't exists";
    String actualMessage = exception.getMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

  @Test
  @DisplayName("Test removing branch successfully")
  void testRemoveBranchSuccess() {
    Branch branchA =
        Branch.builder().id(4).address("Address 4.").city("City 5.").zip("1234656").build();
    doReturn(true).when(mockRepository).existsById(branchA.getId());
    doNothing().when(mockRepository).deleteById(branchA.getId());

    branchService.removeBranch(branchA.getId());

    verify(mockRepository,times(1)).deleteById(branchA.getId());
  }


  @Test
  @DisplayName("Test removing branch fails WHEN branch doesn't exists")
  void testRemoveBranchFailsAndThrowsResourceNotFoundException() {
    Branch branchA =
        Branch.builder().id(4).address("Address 4.").city("City 5.").zip("1234656").build();
    doThrow(new ResourceNotFoundException(
        "Branch with id: " + branchA.getId() + " doesn't exists")).when(mockRepository).existsById(
        branchA.getId());

    Exception exception = Assertions.assertThrows(ResourceNotFoundException.class, () -> {
      branchService.updateBranch(branchA);
    });

    String expectedMessage = "Branch with id: 4 doesn't exists";
    String actualMessage = exception.getMessage();

    Assertions.assertEquals(actualMessage, expectedMessage);
  }

}
