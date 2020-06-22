package org.example.payment.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.payment.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import javax.validation.ConstraintViolationException;

import static org.example.payment.constant.ErrorCode.ILLEGAL_ARGUMENT;
import static org.example.payment.constant.ErrorCode.UNKNOWN_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleException(Exception ex) {
    log.error(ex.getMessage(), ex);
    return new ErrorResponse(UNKNOWN_ERROR);
  }
  @ExceptionHandler(value = {
      WebExchangeBindException.class,
      MethodArgumentNotValidException.class,
      MissingServletRequestParameterException.class,
      ConstraintViolationException.class
  })
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBindException(Exception ex) {
    log.error(ex.getMessage(), ex);
    return new ErrorResponse(ILLEGAL_ARGUMENT);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleIllegalArgumentException(IllegalArgumentException ex) {
    log.error(ex.getMessage(), ex);
    return new ErrorResponse(ILLEGAL_ARGUMENT, ex.getMessage());
  }
}
