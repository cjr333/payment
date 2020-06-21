package org.example.payment.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ErrorCode {
  UNKNOWN_ERROR(-1),
  ILLEGAL_ARGUMENT(4001),
  ;

  @Getter private int code;
}
