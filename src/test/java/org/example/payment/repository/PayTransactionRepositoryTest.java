package org.example.payment.repository;

import org.example.payment.constant.TransactionType;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.entity.PaymentEntity;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("local")
class PayTransactionRepositoryTest {
  @Autowired private PayTransactionRepository payTransactionRepository;

  @Test
  void successSave() {
    PayTransactionEntity payTransactionEntity = PayTransactionEntity.builder()
        .transactionId("paymentId-123")
        .paymentId("paymentId-123")
        .transactionType(TransactionType.PAYMENT)
        .transactionDateMs(System.currentTimeMillis())
        .installment(0)
        .amount(1000L)
        .tax(10L)
        .remainAmount(1000L)
        .remainTax(10L)
        .build();
    PayTransactionEntity saved = payTransactionRepository.save(payTransactionEntity);
    assertTrue(new ReflectionEquals(payTransactionEntity, "version").matches(saved));
  }
}