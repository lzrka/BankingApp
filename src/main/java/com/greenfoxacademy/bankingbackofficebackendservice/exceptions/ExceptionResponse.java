package com.greenfoxacademy.bankingbackofficebackendservice.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ExceptionResponse {

  private String message;
  private Integer code;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String detail;
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Map<String, String> validationErrors;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
  private LocalDateTime timestamp;

}
