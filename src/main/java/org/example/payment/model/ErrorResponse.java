package org.example.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.payment.constant.ErrorCode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
  private int errorCode;
  private String errorMessage;

  public ErrorResponse(ErrorCode errorCode) {
    this.errorCode = errorCode.getCode();
    this.errorMessage = errorCode.name();
  }

  public ErrorResponse(ErrorCode errorCode, String errorMessage) {
    this.errorCode = errorCode.getCode();
    this.errorMessage = errorMessage;
  }
}