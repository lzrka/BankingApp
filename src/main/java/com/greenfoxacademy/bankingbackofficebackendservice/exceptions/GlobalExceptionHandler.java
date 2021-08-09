package com.greenfoxacademy.bankingbackofficebackendservice.exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ExceptionResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    ExceptionResponse response = ExceptionResponse.builder().message(ex.getMessage()).code(404)
        .timestamp(LocalDateTime.now()).build();

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  public ResponseEntity<ExceptionResponse> handleResourceAlreadyExists(
      ResourceAlreadyExistsException ex) {
    ExceptionResponse response = ExceptionResponse.builder().message(ex.getMessage()).code(409)
        .timestamp(LocalDateTime.now()).build();

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(BadResourceException.class)
  public ResponseEntity<ExceptionResponse> handleBadResource(BadResourceException ex) {
    ExceptionResponse response = ExceptionResponse.builder().message(ex.getMessage()).code(400)
        .timestamp(LocalDateTime.now()).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ExceptionResponse response =
        ExceptionResponse.builder().message("Invalid Request").code(400).validationErrors(errors)
            .timestamp(LocalDateTime.now()).build();

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ExceptionResponse> handleConstraintViolations(
      ConstraintViolationException ex) {

    ExceptionResponse response;

    String message = ex.getConstraintName();

    Optional<String> uniqueConstraintMessage = constraintViolationMessages().entrySet().stream()
        .filter(e -> message.contains(e.getKey()))
        .findFirst()
        .map(Map.Entry::getValue);

    if (uniqueConstraintMessage.isPresent()) {
      response = ExceptionResponse.builder().message("DB Constraint violation").code(400)
          .timestamp(LocalDateTime.now())
          .detail(uniqueConstraintMessage.get())
          .build();
    } else {
      response = ExceptionResponse.builder().message("DB Constraint Violation").code(400)
          .timestamp(LocalDateTime.now()).detail(ex.getMessage()).build();
    }

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  private Map<String, String> constraintViolationMessages() {
    return Stream.of(new String[][] {
        {"UNIQUECITYANDADDRESS", "Already existing Branch in the same City & Address"},
        {"UNIQUEPIN", "Client already exists -> Personal Identification Number taken"},
    }).collect(Collectors.toMap(data -> data[0], data -> data[1]));
  }

}
