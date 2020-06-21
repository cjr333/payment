package org.example.payment.repository;

import org.example.payment.constant.TransactionType;
import org.example.payment.entity.PayTransactionEntity;
import org.example.payment.util.SampleGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Profile("local")
class PayTransactionRepositoryTest {
  @Autowired private PayTransactionRepository payTransactionRepository;

  @Test
  void successSave() {
    PayTransactionEntity payTransactionEntity = SampleGenerator.genPayTransactionEntity(TransactionType.PAYMENT);
    PayTransactionEntity saved = payTransactionRepository.save(payTransactionEntity);
    assertTrue(new ReflectionEquals(payTransactionEntity, "version").matches(saved));
  }
}