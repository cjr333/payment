package org.example.payment.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.payment.constant.TransactionType;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayTransaction {
  private String transactionId;

  private TransactionType transactionType;

  private Long amount;

  private Long tax;

  // region for operators
  private String cardNum;

  private String valid;

  private String cvc;
  // endregion
}
