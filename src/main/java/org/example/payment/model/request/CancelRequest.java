package org.example.payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.payment.constant.TransactionType;
import org.example.payment.entity.PayTransactionEntity;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancelRequest {
  @NotNull
  @Size(min = 20, max = 20)
  private String paymentId;

  @NotNull
  @Min(100)
  @Max(1000000000)
  private Long amount;

  private Long tax;

  public PayTransactionEntity toPayTransactionEntity() {
    if (tax == null) {
      tax = Math.round(amount / (double)11);
    }

    return PayTransactionEntity.builder()
        .paymentId(paymentId)
        .transactionType(TransactionType.CANCEL)
        .installment(0)
        .amount(amount)
        .tax(tax)
        .build();
  }
}
