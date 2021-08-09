package com.greenfoxacademy.bankingbackofficebackendservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.greenfoxacademy.bankingbackofficebackendservice.models.Branch;
import com.greenfoxacademy.bankingbackofficebackendservice.services.BranchService;
import io.swagger.annotations.ApiOperation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@PreAuthorize(value = "hasRole('ROLE_Api.ADMIN')")
@RequestMapping("/api")
public class BranchController {

  @Autowired
  private BranchService branchService;

  @ApiOperation(value = "View a list of all the Branches")
  @GetMapping("/branches")
  public ResponseEntity<List<Branch>> branches() {
    return new ResponseEntity<>(branchService.getAllBranches(), HttpStatus.OK);
  }

  @ApiOperation(value = "Retrieve a branch with an ID")
  @GetMapping("/branches/{id}")
  public ResponseEntity<Branch> branchById(@PathVariable Integer id) {
    return new ResponseEntity<>(branchService.getBranchById(id), HttpStatus.OK);
  }

  @ApiOperation(value = "Delete a branch with an ID")
  @DeleteMapping("/branches/{id}")
  public ResponseEntity<Void> deleteBranch(@PathVariable Integer id) {
    branchService.removeBranch(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @ApiOperation(value = "Add a new branch")
  @PostMapping("/branches")
  public ResponseEntity<Branch> createBranch(@Valid @RequestBody Branch branch)
      throws URISyntaxException {
    Branch savedBranch = branchService.createBranch(branch);

    return ResponseEntity.created(new URI("/api/branches/" + savedBranch.getId()))
        .body(savedBranch);
  }

  @ApiOperation(value = "Update existing Branch with an ID")
  @PatchMapping(path = "/branches/{id}", consumes = "application/json-patch+json")
  public ResponseEntity<Branch> updateBranchWithPatch(@RequestBody JsonPatch patch,
                                                      @PathVariable Integer id) {
    try {
      Branch branch = branchService.getBranchById(id);
      Branch patchedBranch = applyPatchToBranch(patch, branch);
      branchService.updateBranch(patchedBranch);
      return ResponseEntity.ok(patchedBranch);
    } catch (JsonPatchException | JsonProcessingException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private Branch applyPatchToBranch(
      JsonPatch patch, Branch branch) throws JsonPatchException, JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode patched =
        patch.apply(objectMapper.convertValue(branch, JsonNode.class));
    return objectMapper.treeToValue(patched, Branch.class);
  }
}
