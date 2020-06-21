package org.example.payment.repository;

import org.example.payment.entity.PaymentEntity;
import org.example.payment.entity.SampleEntityGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Profile("local")
class PaymentRepositoryTest {
  @Autowired private PaymentRepository paymentRepository;

  @Test
  void successSave() {
    PaymentEntity paymentEntity = SampleEntityGenerator.genPaymentEntity();
    PaymentEntity saved = paymentRepository.save(paymentEntity);
    assertTrue(new ReflectionEquals(paymentEntity, "version").matches(saved));
  }
}