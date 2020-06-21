package org.example.payment.entity;

public class SampleEntityGenerator {
  public static CreditCardEntity genCreditCardEntity() {
    return CreditCardEntity.builder()
        .cardNum("1234567890123456")
        .validThru("1228")
        .cvc("123")
        .lastPayDateMs(System.currentTimeMillis())
        .build();
  }

  public static PaymentEntity genPaymentEntity() {
    return PaymentEntity.builder()
        .paymentId("paymentId-123")
        .cardNum("1234567890123456")
        .payDateMs(System.currentTimeMillis())
        .installment(0)
        .orgAmount(1000L)
        .orgTax(10L)
        .remainAmount(1000L)
        .remainTax(10L)
        .build();
  }
}
