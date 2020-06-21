package org.example.payment.entity;

import org.example.payment.constant.TransactionType;

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

  public static PayTransactionEntity genPayTransactionEntity(TransactionType transactionType) {
    long amount = 1000L;
    long tax = 10L;
    String transactionId = "transactionId-123";
    String paymentId = "paymentId-123";
    return PayTransactionEntity.builder()
        .transactionId(transactionId)
        .paymentId(transactionType == TransactionType.PAYMENT ? transactionId : paymentId)
        .transactionType(transactionType)
        .transactionDateMs(System.currentTimeMillis())
        .installment(transactionType == TransactionType.PAYMENT ? 12 : 0)
        .amount(amount)
        .tax(tax)
        .remainAmount(transactionType == TransactionType.PAYMENT ? amount : amount / 2)
        .remainTax(transactionType == TransactionType.PAYMENT ? tax : tax / 2)
        .build();
  }
}
