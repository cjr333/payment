package org.example.payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.example.payment.constant.TransactionType;
import org.example.payment.entity.CreditCardEntity;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.entity.PaymentEntity;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayRequest {
  @NotNull
  @Size(min = 10, max = 16)
  private String cardNum;

  @NotNull
  @Size(min = 4, max = 4)
  private String validThru;

  @NotNull
  @Size(min = 3, max = 3)
  private String cvc;

  @NotNull
  @Min(0)
  @Max(12)
  private Integer installment;

  @NotNull
  @Min(100)
  @Max(1000000000)
  private Long amount;

  private Long tax;

  @AssertTrue
  public boolean isValidCardNum() {
    return StringUtils.isNumeric(cardNum);
  }

  @AssertTrue
  public boolean isValidValidThru() {
    if (StringUtils.isNumeric(validThru)) {
      int parsed = Integer.parseInt(validThru);
      return parsed >= 100 && parsed <= 1299;    // 0100 ~ 1299
    }
    return false;
  }

  @AssertTrue
  public boolean isValidCvc() {
    return StringUtils.isNumeric(cvc);
  }

  @AssertTrue
  public boolean isValidTax() {
    if (amount == null) {
      return false;   // amount 가 null 일 경우 이 메서드가 호출되지 않고 리턴되는 것이 아님.
    }
    if (tax == null) {
      tax = Math.round(amount / (double)11);
    }
    return tax <= amount;
  }

  public CreditCardEntity toCreditCardEntity() {
    return CreditCardEntity.builder()
        .cardNum(cardNum)
        .validThru(validThru)
        .cvc(cvc)
        .build();
  }

  public PaymentEntity toPaymentEntity() {
    return PaymentEntity.builder()
        .cardNum(cardNum)
        .installment(installment)
        .remainAmount(amount)
        .remainTax(tax)
        .orgAmount(amount)
        .orgTax(tax)
        .build();
  }

  public PayTransactionEntity toPayTransactionEntity() {
    return PayTransactionEntity.builder()
        .transactionType(TransactionType.PAYMENT)
        .installment(installment)
        .amount(amount)
        .tax(tax)
        .remainAmount(amount)
        .remainTax(tax)
        .build();
  }
}
