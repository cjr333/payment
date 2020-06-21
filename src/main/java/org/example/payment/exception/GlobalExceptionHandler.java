package org.example.payment.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMsg handleBindException(WebExchangeBindException ex) {
    log.error(ex.getMessage(), ex);
    return new ErrorMsg("Invalid parameter");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorMsg handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error(ex.getMessage(), ex);
    return new ErrorMsg(ex.getMessage());
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class ErrorMsg {
    private String message;
  }
}
