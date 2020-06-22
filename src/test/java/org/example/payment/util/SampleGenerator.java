package org.example.payment.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.example.payment.constant.TransactionType;
import org.example.payment.entity.CreditCardEntity;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.entity.PaymentEntity;
import org.example.payment.model.PayTransaction;
import org.example.payment.model.request.CancelRequest;
import org.example.payment.model.request.PayRequest;

public class SampleGenerator {
  public static CreditCardEntity genCreditCardEntity() {
    return CreditCardEntity.builder()
        .cardNum(RandomStringUtils.randomNumeric(10, 17))
        .validThru("1228")
        .cvc("123")
        .lastPayDateMs(System.currentTimeMillis())
        .build();
  }

  public static PaymentEntity genPaymentEntity() {
    return PaymentEntity.builder()
        .paymentId(RandomStringUtils.randomAlphanumeric(20))
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
    String transactionId = RandomStringUtils.randomAlphanumeric(20);
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

  public static PayRequest genPayRequest() {
    return PayRequest.builder()
        .cardNum("123456789012345")
        .validThru("1228")
        .cvc("123")
        .amount(10000L)
        .tax(1000L)
        .installment(0)
        .build();
  }

  public static CancelRequest genCancelRequest() {
    PaymentEntity paymentEntity = genPaymentEntity();
    return genCancelRequest(paymentEntity);
  }

  public static CancelRequest genCancelRequest(PaymentEntity paymentEntity) {
    return CancelRequest.builder()
        .paymentId(paymentEntity.getPaymentId())
        .amount(paymentEntity.getRemainAmount() / 2)
        .tax(paymentEntity.getRemainTax() / 2)
        .build();
  }

  public static PayTransaction genPayTransaction(TransactionType transactionType, boolean isOperator) {
    long amount = 10000L;
    long tax = 1000L;
    PayTransaction.PayTransactionBuilder payTransactionBuilder = PayTransaction.builder()
        .transactionId(RandomStringUtils.randomAlphanumeric(20))
        .transactionType(transactionType)
        .amount(amount)
        .tax(tax);
    if (isOperator) {
      payTransactionBuilder = payTransactionBuilder
          .remainAmount(transactionType == TransactionType.PAYMENT ? amount : amount / 2)
          .remainTax(transactionType == TransactionType.PAYMENT ? tax : tax / 2)
          .cardNum("123456******890")
          .validThru("1228")
          .cvc("123");
    }
    return payTransactionBuilder.build();
  }
}
